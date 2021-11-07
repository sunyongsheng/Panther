const imageList = {
    template: `<el-main>
                <el-button type="success" icon="el-icon-upload" size="medium" @click="onClickUpload">上传图片</el-button>
                <el-button type="info" icon="el-icon-tickets" size="medium" @click="onClickRefreshDb">刷新数据库</el-button>
                <el-button type="primary" icon="el-icon-files" size="medium" @click="onClickRefreshFile">刷新本地目录</el-button>
                <el-button type="danger" icon="el-icon-delete-solid" size="medium" @click="onClickDelete" v-if="multipleSelection.length > 0">删除</el-button>

                <div class="space-column-20"></div>
                <el-table
                        :data="imageData"
                        v-loading="loadingImageData"
                        style="width: 100%;"
                        @selection-change="onSelectionChange">
                    <el-table-column type="selection" width="55"></el-table-column>
                    <el-table-column label="预览" align="center" width="100">
                        <template slot-scope="scope">
                            <el-image :src="scope.row.url" :preview-src-list="previewImages" fit="contain"></el-image>
                        </template>
                    </el-table-column>
                    <el-table-column prop="name" label="名称" align="center">
                        <template slot-scope="scope">
                            <el-tooltip class="item" effect="dark" :content="'点击复制：' + scope.row.name" placement="top">
                                <el-link @click="onClickImageName(scope.row)">{{ scope.row.nameEllipsis }}</el-link>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                    <el-table-column prop="url" label="图片URL" align="center" min-width="140">
                        <template slot-scope="scope">
                            <el-tooltip class="item" effect="dark" :content="'点击复制：' + scope.row.url" placement="top">
                                <el-link @click="onClickCopyImageUrl(scope.row)">{{ scope.row.urlEllipsis }}</el-link>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                    <el-table-column prop="ownerApp" label="所属App" align="center"></el-table-column>
                    <el-table-column label="状态" align="center">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.deleted" type="danger">回收站</el-tag>
                            <el-tag v-else type="success">正常</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="uploadTimeFormatted" label="上传时间" align="center"></el-table-column>
                    <el-table-column prop="sizeFormatted" label="图片大小" align="center"></el-table-column>
                    <el-table-column label="操作" align="center">
                        <template slot-scope="scope">
                            <el-button :type="scope.row.deleted ? 'warning' : 'danger'" size="mini" @click="onClickDeleteImage(scope.row)">
                                {{scope.row.deleted ? '恢复' : '删除'}}
                            </el-button>
                        </template>
                    </el-table-column>
                </el-table>
                <div class="space-column-20"></div>
                <el-pagination v-if="totalPage > 1"
                               background
                               layout="prev, pager, next"
                               :page-count="totalPage"
                               :current-page="currPage"
                               @current-change="onChangePage">
                </el-pagination>
            </el-main>`,
    data() {
        return {
            // 表格数据
            imageData: [],
            previewImages: [],
            loadingImageData: false,
            currPage: 1,
            totalPage: 1,
            pageSize: 10,
            // 多选的图片
            multipleSelection: [],
            // 上传图片
            uploadDialogVisible: false,
            uploading: false,
            uploadData: {
                selectedImages: [],
                uploadDir: '',
                uploadDirDisabled: false,
                uploadApp: {},
            },
            appData: [],
            searching: false,
            selectedImagesPreview: [],
            // 刷新数据库
            refreshDbDialogVisible: false,
            invalidDbItems: [],
            // 刷新本地目录
            refreshFileDialogVisible: false,
            invalidFileItems: [],

            refreshing: false,
            processing: false,
        }
    },
    methods: {
        fetchImageData() {
            this.loadingImageData = true;
            let fetchedData = []
            getAllImages(this.currPage - 1, this.pageSize).then(response => {
                this.totalPage = response.data.totalPages;
                response.data.content.forEach(image => {
                    image.nameEllipsis = ellipsisText(image.name, 20);
                    image.urlEllipsis = ellipsisText(image.url, 40);
                    image.uploadTimeFormatted = formatDate(new Date(image.uploadTime), "yyyy-MM-dd hh:mm:ss");
                    image.sizeFormatted = getSizeFromByte(image.size);
                    image.deleted = image.status === 'DELETED';
                    fetchedData.push(image);
                });
                this.imageData = fetchedData;
                this.imageData.forEach(image => {
                    this.previewImages.push(image.url);
                })
            }).catch(e => {
                this.$message.error(e.msg)
            }).finally(_ => {
                this.loadingImageData = false;
            });
        },
        onClickUpload() {
            this.uploadDialogVisible = true;
        },
        onClickRefreshDb() {
            this.$confirm("此操作将扫描整个数据库，是否继续", "提示").then(() => {
                this.refreshDbDialogVisible = true;
                this.refreshing = true;
                refreshImageDb().then(response => {
                    this.invalidDbItems = response.data.invalidItems;
                }).catch((e) => {
                    this.$message.error(e.msg);
                }).finally(() => {
                    this.refreshing = false;
                });
            });
        },
        onClickRefreshFile() {
            this.$confirm("此操作将扫描存储目录下的所有文件，并判断是否在数据库中有对应记录，可能会消耗较多内存，是否继续", "提示").then(() => {
                this.refreshFileDialogVisible = true;
                this.refreshing = true;
                refreshImageFile().then(response => {
                    this.invalidFileItems = response.data.invalidItems;
                }).catch((e) => {
                    this.$message.error(e.msg);
                }).finally(() => {
                    this.refreshing = false;
                });
            });
        },
        onClickDelete() {
            this.$confirm(`你将删除${this.multipleSelection.length}张图片。图片删除后将暂存在回收站中，30天后自动删除，是否继续？`, '警告', {
                confirmButtonClass: 'delete-action-confirm',
                confirmButtonText: '删除',
                cancelButtonText: '取消',
                type: 'error'
            }).then(() => {
                const tasks = [];
                this.multipleSelection.forEach(image => {
                    tasks.push(new Promise((resolve, reject) => {
                        deleteImage(image.id).then(response => {
                            this.$message.success(response.msg);
                            resolve.call(response);
                        }).catch(error => {
                            this.$message.error(error.msg);
                            reject.call(error);
                        });
                    }));
                });
                Promise.all(tasks).finally(() => this.fetchImageData());
            });
        },
        parseFile(event) {
            if (event.target.files.length > 0) {
                let that = this;
                let i = 0, len = event.target.files.length;
                for (i, len; i < len; i++) {
                    const file = event.target.files[i]
                    this.uploadData.selectedImages.push(file);
                    const reader = new FileReader();
                    reader.onload = function (evt) {
                        that.selectedImagesPreview.push(evt.target.result);
                    }
                    reader.readAsDataURL(file)
                }
            }
        },
        onClickUploadRemove(image) {
            let toRemoveIndex = -1;
            for (let i = 0; i < this.selectedImagesPreview.length; i++) {
                if (image === this.selectedImagesPreview[i]) {
                    toRemoveIndex = i;
                    break;
                }
            }
            if (toRemoveIndex !== -1) {
                this.uploadData.selectedImages.splice(toRemoveIndex, 1);
                this.selectedImagesPreview.splice(toRemoveIndex, 1);
            }
        },
        getAppData(query) {
            let fetched = [];
            this.searching = true;
            if (query) {
                searchApp(query).then(response => {
                    response.data.forEach(app => {
                        fetched.push(app);
                    });
                    this.appData = fetched;
                }).finally(() => this.searching = false);
            } else {
                getAllAppData(0, 50).then(response => {
                    response.data.content.forEach(app => {
                        fetched.push(app);
                    });
                    this.appData = fetched;
                }).finally(() => this.searching = false);
            }
        },
        onChangeSelectedApp(app) {
            this.uploadData.uploadDir = '/app/' + app.englishName;
            this.uploadData.uploadDirDisabled = app.role !== 'SUPER';
        },
        clickUpload() {
            if (!this.uploadData.uploadApp.appKey) {
                this.$message.error("请选择图片所属App");
                return;
            }
            if (this.uploadData.selectedImages.length === 0) {
                this.$message.error("请选择上传的图片");
                return;
            }
            this.uploading = true;
            const tasks = [];
            let dir;
            if (this.uploadData.uploadApp.role === 'SUPER') {
                if (this.uploadData.uploadDir !== '/app/' + this.uploadData.uploadApp.englishName) {
                    dir = this.uploadData.uploadDir;
                }
            } else {
                dir = null;
            }
            this.uploadData.selectedImages.forEach(file => {
                tasks.push(new Promise(((resolve, reject) => {
                    upload(this.uploadData.uploadApp.appKey, dir, file).then(response => {
                        this.$message.success(`上传${response.data.originalName}成功`);
                        resolve.call(response);
                    }).catch((error) => {
                        this.$message.error(error.msg);
                        reject.call(error);
                    });
                })));
            });
            Promise.all(tasks).then((_) => {
                this.uploadDialogVisible = false;
                this.uploadData.selectedImages = [];
                this.uploadData.uploadDir = '';
                this.uploadData.uploadApp = {};
                this.selectedImagesPreview = [];
            }).catch(error => {
                console.error(error)
            }).finally(() => {
                this.uploading = false;
                this.fetchImageData();
            });
        },
        clickDeleteInvalidImg(image) {
            deleteImageForever(image.id, false).then(response => {
                this.$message.success(response.msg);
                const index = findFirstIndex(this.invalidDbItems, (element) => image.id === element.id);
                this.invalidDbItems.splice(index, 1);
            }).catch(error => {
                this.$message.error(error.msg);
            });
        },
        clickDeleteInvalidFile(file) {
            this.$confirm('此图片将从磁盘中永久删除，是否继续？', '警告', {
                confirmButtonClass: 'delete-action-confirm',
                confirmButtonText: '删除',
                cancelButtonText: '取消',
                type: 'error'
            }).then(() => {
                deleteFileForever(file.absolutePath).then(response => {
                    this.$message.success(response.msg);
                    const index = findFirstIndex(this.invalidFileItems, (element) => file.relativePath === element.relativePath);
                    this.invalidFileItems.splice(index, 1);
                }).catch(error => {
                    this.$message.error(error.msg);
                });
            });
        },
        clickSaveInvalidFile() {
            this.$confirm('未删除的图片将在数据库中生成记录，对于未知归属App的图片建议手动创建App或者将其移动至正确的文件目录下再进行此操作', '提示', {
                confirmButtonText: '确认',
                cancelButtonText: '取消',
            }).then(() => {
                this.processing = true;
                saveInvalidFiles(this.invalidFileItems).then(response => {
                    this.$message.success(response.msg);
                    this.refreshFileDialogVisible = false;
                    this.fetchImageData();
                }).catch(e => {
                    this.$message.error(e.msg);
                }).finally(() => {
                    this.processing = true;
                });
            });
        },
        onClickImageName(image) {
            try {
                navigator.clipboard.writeText(image.name);
                this.$notify({
                    title: '复制成功',
                    duration: 2000,
                    type: 'success'
                });
            } catch (err) {
                this.$notify.error({
                    title: '复制失败，请手动复制',
                    message: image.name,
                    duration: 0
                });
            }
        },
        onClickCopyImageUrl(image) {
            try {
                navigator.clipboard.writeText(image.url);
                this.$notify({
                    title: '复制成功',
                    duration: 2000,
                    type: 'success'
                });
            } catch (err) {
                this.$notify.error({
                    title: '复制失败，请手动复制',
                    message: image.url,
                    duration: 0
                });
            }
        },
        onClickDeleteImage(image) {
            if (image.deleted) {
                this.$confirm('确认恢复图片吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    undeleteImage(image.id).then(response => {
                        this.$message.success(response.msg);
                        this.fetchImageData();
                    }).catch(e => {
                        this.$message.error(e.msg);
                    });
                });
            } else {
                this.$confirm('图片删除后将暂存在回收站中，30天后自动删除，是否继续？', '警告', {
                    confirmButtonClass: 'delete-action-confirm',
                    confirmButtonText: '删除',
                    cancelButtonText: '取消',
                    type: 'error'
                }).then(() => {
                    deleteImage(image.id).then(response => {
                        this.$message.success(response.msg);
                        this.fetchImageData();
                    }).catch(e => {
                        this.$message.error(e.msg);
                    });
                });
            }
        },
        onSelectionChange(val) {
            this.multipleSelection = val;
        },
        onChangePage(page) {
            this.currPage = page;
            this.fetchImageData();
        }
    },
    created() {
        this.fetchImageData();
    }
}