async function install(param) {
    return axiosClient.post("/install", param);
}