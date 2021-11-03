async function getAllImages(page, pageSize) {
    return axiosClient.get(`/api/v1/images?page=${page}&page_size=${pageSize}`)
}

async function deleteImage(imageId) {
    return axiosClient.post(`/api/v1/image/delete/${imageId}`)
}

async function undeleteImage(imageId) {
    return axiosClient.post(`/api/v1/image/undelete/${imageId}`)
}

async function refreshImageDb() {
    return axiosClient.get("/api/v1/image/refresh/db")
}

async function deleteImageForever(id, deleteFile) {
    return axiosClient.delete(`/api/v1/admin/image?id=${id}&delete_file=${deleteFile}`)
}

async function refreshImageFile() {
    return axiosClient.get("/api/v1/image/refresh/file")
}

async function deleteFileForever(absPath) {
    return axiosClient.post("/api/v1/admin/file/delete", {
        key: absPath
    })
}

async function saveInvalidFiles(files) {
    return axiosClient.post(`/api/v1/admin/file/save`, {
        values: files
    })
}
