async function getAllAppData(page, pageSize) {
    return axiosClient.get(`/api/v1/apps?page=${page}&page_size=${pageSize}`)
}

async function hasGeneratedToken(appKey) {
    return axiosClient.get(`api/v1/app/uploadToken?app_key=${appKey}`)
}

async function generateUploadToken(appKey) {
    return axiosClient.post(`api/v1/app/uploadToken?app_key=${appKey}`)
}

async function updateAppInfo(appKey, param) {
    return axiosClient.put(`/api/v1/app/?app_key=${appKey}`, param)
}

async function lockApp(appKey) {
    return axiosClient.post(`/api/v1/app/lock/?app_key=${appKey}`)
}

async function unlockApp(appKey) {
    return axiosClient.post(`/api/v1/app/unlock/?app_key=${appKey}`)
}

async function deleteApp(appKey) {
    return axiosClient.post(`/api/v1/app/delete/?app_key=${appKey}`)
}

async function undeleteApp(appKey) {
    return axiosClient.post(`/api/v1/app/undelete/?app_key=${appKey}`)
}