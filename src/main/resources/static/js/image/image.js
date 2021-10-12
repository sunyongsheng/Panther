async function getAllImages(page, pageSize) {
    return axiosClient.get(`/api/v1/images?page=${page}&page_size=${pageSize}`)
}

async function deleteImage(imageId) {
    return axiosClient.post(`/api/v1/image/delete/${imageId}`)
}

async function undeleteImage(imageId) {
    return axiosClient.post(`/api/v1/image/undelete/${imageId}`)
}
