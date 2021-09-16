async function shutdown() {
    return axiosClient.get("/shutdown");
}