async function upload(appKey, dir, file) {
    const config = {
        headers: {
            'Content-Type': 'multipart/form-data'
        }
    }
    const formData = new FormData();
    formData.append("app_key", appKey);
    formData.append("file", file);
    if (dir) {
        formData.append("dir", dir);
    }
    return axiosClient.post("/api/v1/admin/image", formData, config);
}

async function getAdminInfo() {
    return axiosClient.get(`/api/v1/admin/info`)
}
