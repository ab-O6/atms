const defaultBaseUrl = "http://localhost:8080";

export function getApiBaseUrl(): string {
  const configured = import.meta.env.VITE_API_BASE_URL;
  if (configured && configured.length > 0) {
    return configured.replace(/\/$/, "");
  }
  return defaultBaseUrl;
}

export class ApiError extends Error {
  readonly status: number;
  readonly problem?: ProblemDetailBody;

  constructor(status: number, message: string, problem?: ProblemDetailBody) {
    super(message);
    this.status = status;
    this.problem = problem;
  }
}

export type ProblemDetailBody = {
  type?: string;
  title?: string;
  status?: number;
  detail?: string;
  instance?: string;
};

export async function apiFetch<T>(
  path: string,
  init?: RequestInit,
): Promise<T> {
  const url = `${getApiBaseUrl()}${path.startsWith("/") ? path : `/${path}`}`;
  const response = await fetch(url, {
    ...init,
    headers: {
      Accept: "application/json",
      ...init?.headers,
    },
  });

  const contentType = response.headers.get("content-type") ?? "";
  const isJson = contentType.includes("application/json");
  const isProblem = contentType.includes("application/problem+json");

  if (!response.ok) {
    let problem: ProblemDetailBody | undefined;
    if (isJson || isProblem) {
      problem = (await response.json()) as ProblemDetailBody;
    }
    const detail = problem?.detail ?? response.statusText;
    throw new ApiError(response.status, detail, problem);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  if (!isJson) {
    return undefined as T;
  }

  return (await response.json()) as T;
}
