export function getApiErrorMessage(error, fallback = "Thao tác thất bại") {
    const data = error.response?.data;

    if (typeof data === "string") {
        return data;
    }

    return data?.message || data?.detail || fallback;
}
