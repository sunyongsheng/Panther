async function getAllAppData(page, pageSize) {
    return axiosClient.get(`/api/v1/apps?page=${page}&page_size=${pageSize}`)
}