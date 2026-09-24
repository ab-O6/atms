package com.atms.ticket.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.atms.ticket.domain.Priority;
import com.atms.ticket.domain.TicketStatus;
import com.atms.ticket.infrastructure.persistence.TicketEntity;
import com.atms.ticket.infrastructure.persistence.TicketRepository;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TransitionServiceTest {

    private static final String DISPLAY_ID = "TKT-0001";

    private static final Set<String> ALLOWED_TRANSITIONS = Set.of(
            "OPEN,IN_PROGRESS",
            "OPEN,CANCELLED",
            "IN_PROGRESS,RESOLVED",
            "IN_PROGRESS,CANCELLED",
            "RESOLVED,CLOSED");

    static Stream<Arguments> forbiddenTransitionMatrix() {
        return Arrays.stream(TicketStatus.values())
                .flatMap(from -> Arrays.stream(TicketStatus.values())
                        .filter(to -> from != to)
                        .filter(to -> !ALLOWED_TRANSITIONS.contains(from.name() + "," + to.name()))
                        .map(to -> Arguments.of(from, to)));
    }

    static Stream<Arguments> allowedTransitionMatrix() {
        return ALLOWED_TRANSITIONS.stream()
                .map(edge -> {
                    String[] parts = edge.split(",");
                    return Arguments.of(TicketStatus.valueOf(parts[0]), TicketStatus.valueOf(parts[1]));
                });
    }

    private final Map<String, TicketEntity> tickets = new HashMap<>();
    private final AtomicInteger saveCount = new AtomicInteger();
    private final AtomicInteger reindexCount = new AtomicInteger();
    private TicketRepository ticketRepository;
    private TransitionService transitionService;

    @BeforeEach
    void setUp() {
        tickets.clear();
        saveCount.set(0);
        reindexCount.set(0);
        ticketRepository = repositoryProxy();
        TicketKnowledgeIndexPort indexPort = snapshot -> reindexCount.incrementAndGet();
        transitionService = new TransitionService(ticketRepository, indexPort);
    }

    @ParameterizedTest
    @MethodSource("allowedTransitionMatrix")
    void allowedTransitions_updateStatusAndReindex(TicketStatus from, TicketStatus to) {
        seedTicket(ticketInStatus(from));

        String notes = to == TicketStatus.RESOLVED ? "fix applied" : null;
        TicketEntity result = transitionService.transition(DISPLAY_ID, to, notes);

        assertThat(result.getStatus()).isEqualTo(to);
        if (to == TicketStatus.RESOLVED) {
            assertThat(result.getResolutionNotes()).isEqualTo("fix applied");
        }
        assertThat(saveCount.get()).isEqualTo(1);
        assertThat(reindexCount.get()).isEqualTo(1);
    }

    @ParameterizedTest
    @MethodSource("forbiddenTransitionMatrix")
    void forbiddenTransitions_throwInvalidTransition(TicketStatus from, TicketStatus to) {
        seedTicket(ticketInStatus(from));
        String notes = to == TicketStatus.RESOLVED ? "fix applied" : null;

        assertThatThrownBy(() -> transitionService.transition(DISPLAY_ID, to, notes))
                .isInstanceOf(InvalidTicketTransitionException.class);

        assertThat(saveCount.get()).isZero();
        assertThat(reindexCount.get()).isZero();
    }

    @Test
    void resolveWithoutNotes_throwsResolutionNotesRequired() {
        seedTicket(ticketInStatus(TicketStatus.IN_PROGRESS));

        assertThatThrownBy(() -> transitionService.transition(DISPLAY_ID, TicketStatus.RESOLVED, null))
                .isInstanceOf(ResolutionNotesRequiredException.class);
        assertThatThrownBy(() -> transitionService.transition(DISPLAY_ID, TicketStatus.RESOLVED, "   "))
                .isInstanceOf(ResolutionNotesRequiredException.class);

        assertThat(saveCount.get()).isZero();
    }

    @Test
    void sameStatus_isNoOpWithoutSaveOrReindex() {
        TicketEntity ticket = ticketInStatus(TicketStatus.OPEN);
        seedTicket(ticket);

        TicketEntity result = transitionService.transition(DISPLAY_ID, TicketStatus.OPEN, null);

        assertThat(result).isSameAs(ticket);
        assertThat(saveCount.get()).isZero();
        assertThat(reindexCount.get()).isZero();
    }

    private void seedTicket(TicketEntity ticket) {
        tickets.put(DISPLAY_ID, ticket);
    }

    private TicketRepository repositoryProxy() {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) {
                String name = method.getName();
                if ("findByDisplayId".equals(name)) {
                    return Optional.ofNullable(tickets.get((String) args[0]));
                }
                if ("save".equals(name)) {
                    saveCount.incrementAndGet();
                    TicketEntity entity = (TicketEntity) args[0];
                    tickets.put(entity.getDisplayId(), entity);
                    return entity;
                }
                if ("toString".equals(name)) {
                    return "TicketRepository-test-proxy";
                }
                if ("hashCode".equals(name)) {
                    return System.identityHashCode(proxy);
                }
                if ("equals".equals(name)) {
                    return proxy == args[0];
                }
                throw new UnsupportedOperationException("Unexpected repository call: " + name);
            }
        };
        return (TicketRepository)
                Proxy.newProxyInstance(TicketRepository.class.getClassLoader(), new Class[] {TicketRepository.class}, handler);
    }

    private static TicketEntity ticketInStatus(TicketStatus status) {
        TicketEntity ticket = new TicketEntity();
        ticket.setDisplayId(DISPLAY_ID);
        ticket.setTitle("title");
        ticket.setDescription("description");
        ticket.setPriority(Priority.LOW);
        ticket.setAssignee("agent");
        ticket.setCategory("");
        ticket.setStatus(status);
        return ticket;
    }
}
