const appList = {
    template: `<el-main>
                <el-button type="primary" icon="el-icon-plus" size="medium" @click="onClickCreate">创建App</el-button>
                <div class="space-column-20"></div>
                <el-table
                    :data="appData"
                    v-loading="loadingAppData"
                    stripe
                    style="width: 100%;">
                    <el-table-column prop="name" label="名称" align="center"></el-table-column>
                    <el-table-column prop="englishName" label="英文名" align="center"></el-table-column>
                    <el-table-column prop="createTimeFormatted" label="创建时间" align="center"></el-table-column>
                    <el-table-column prop="totalImages" label="图片总数" align="center"></el-table-column>
                    <el-table-column label="状态" align="center" width="150">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.locked" type="warning">已锁定</el-tag>
                            <el-tag v-else-if="scope.row.deleted" type="danger">回收站</el-tag>
                            <el-tag v-else type="success">正常</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column label="类型" align="center" width="150">
                        <template slot-scope="scope">
                            <el-tag v-if="scope.row.superApp">超级App</el-tag>
                            <el-tag v-else type="info">普通</el-tag>
                        </template>
                    </el-table-column>
                    <el-table-column prop="appKeyFake" label="AppKey" v-popover:popover align="center">
                        <template slot-scope="scope">
                            <el-popover trigger="click" placement="top" ref="popover">
                                <p>{{ scope.row.appKey }}</p>
                                <div slot="reference">
                                    <p>{{ scope.row.appKeyFake }}</p>
                                </div>
                            </el-popover>
                        </template>
                    </el-table-column>
                    <el-table-column label="操作" align="center">
                        <template slot-scope="scope">
                            <el-button
                                    type="success"
                                    icon="el-icon-upload2"
                                    @click="onClickUpload(scope.row)" circle></el-button>
                            <el-button
                                    type="primary"
                                    icon="el-icon-edit"
                                    @click="onClickEdit(scope.row)" circle></el-button>

                            <el-button
                                    type="info"
                                    icon="el-icon-setting"
                                    @click="onClickSetting(scope.row)" circle></el-button>
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
    data: function () {
        return {
            totalPage: 1,
            currPage: 1,
            pageSize: 10,

            currSelectApp: {
                appKey: '',
                name: '',
                role: 'NORMAL',
                englishName: '',
                avatarUrl: '',
                createTime: 0,
                phone: '',
                email: '',
                status: 'NORMAL',
                totalImages: 0,
                hasUploadToken1: false,
                updateToken1GenTime: 0,

                superApp: false,
                createTimeFormatted: '',
                locked: false,
            },

            editDialogVisible: false,
            updating: false,

            uploadDialogVisible: false,
            uploading: false,
            selectedFiles: [],
            previewImages: [],

            settingDialogVisible: false,
            tokenValueDialogVisible: false,
            tokenValue: '',
            settingForm: {
                namingStrategy: 'UUID',
            },
            allNamingStrategySample,

            createDialogVisible: false,
            createAppParam: {
                name: '',
                englishName: '',
                superApp: false,
            },

            loadingAppData: false,
            appData: [],
        }
    },
    methods: {
        fetchAppData() {
            this.loadingAppData = true;
            let fetchedData = []
            getAllAppData(this.currPage - 1, this.pageSize).then(response => {
                this.totalPage = response.data.totalPages;
                response.data.content.forEach(app => {
                    app.appKeyFake = "****************";
                    app.superApp = app.role === 'SUPER';
                    app.locked = app.status === 'LOCKED';
                    app.deleted = app.status === 'DELETED';
                    app.createTimeFormatted = formatDate(new Date(app.createTime), "yyyy-MM-dd hh:mm:ss");
                    if (app.hasUploadToken1) {
                        app.updateToken1GenTimeFormatted = '生成于 ' + formatDate(new Date(app.updateToken1GenTime), "yyyy年MM月dd日 hh:mm:ss");
                    } else {
                        app.updateToken1GenTimeFormatted = '还未生成过Token，点击右边按钮生成'
                    }
                    fetchedData.push(app);
                });
                this.appData = fetchedData;
            }).finally(() => {
                this.loadingAppData = false;
            })
        },
        onChangePage(page) {
            this.currPage = page;
            this.fetchAppData();
        },
        onClickUpload(app) {
            this.uploadDialogVisible = true;
            if (this.currSelectApp.appKey !== app.appKey) {
                this.selectedFiles = [];
            }
            Object.assign(this.currSelectApp, app);
        },
        onClickEdit(app) {
            this.editDialogVisible = true;
            Object.assign(this.currSelectApp, app);
        },
        onClickCreate() {
            this.createDialogVisible = true;
            this.createAppParam = {
                name: '',
                englishName: '',
                superApp: false
            };
        },
        onClickSetting(app) {
            this.settingDialogVisible = true;
            this.settingForm.namingStrategy = app.namingStrategy;
            Object.assign(this.currSelectApp, app);
        },
        parseFile(event) {
            if (event.target.files.length > 0) {
                let that = this;
                let i = 0, len = event.target.files.length;
                for (i, len; i < len; i++) {
                    const file = event.target.files[i]
                    this.selectedFiles.push(file);
                    const reader = new FileReader();
                    reader.onload = function (evt) {
                        that.previewImages.push(evt.target.result);
                    }
                    reader.readAsDataURL(file)
                }
            }
        },
        clickUpload() {
            if (this.selectedFiles.length > 0) {
                this.uploading = true;
                const tasks = [];
                this.selectedFiles.forEach(file => {
                    tasks.push(new Promise(((resolve, reject) => {
                        upload(this.currSelectApp.appKey, null, file).then(response => {
                            resolve.call(response);
                        }).catch((error) => {
                            reject.call(error);
                        });
                    })));
                });
                Promise.all(tasks).then((results) => {
                    this.$message.success(`上传了${results.length}张图片`);
                    this.uploadDialogVisible = false;
                    this.selectedFiles = [];
                    this.previewImages = [];
                }).catch(errors => {
                    errors.forEach(error => this.$message.error(error.msg));
                }).finally(() => {
                    this.uploading = false;
                });
            } else {
                this.$message.error('请先选择图片');
            }
        },
        onClickUploadRemove(image) {
            let toRemoveIndex = -1;
            for (let i = 0; i < this.previewImages.length; i++) {
                if (image === this.previewImages[i]) {
                    toRemoveIndex = i;
                    break;
                }
            }
            if (toRemoveIndex !== -1) {
                this.selectedFiles.splice(toRemoveIndex, 1);
                this.previewImages.splice(toRemoveIndex, 1);
            }
        },
        clickDeleteApp(appKey, deleted) {
            if (deleted) {
                this.$confirm('恢复App后，将恢复上传能力，其图片也将从回收站中移动回原目录，确认恢复？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'info'
                }).then(() => {
                    undeleteApp(appKey).then(_ => {
                        this.editDialogVisible = false;
                        this.$message({
                            type: 'success',
                            message: '恢复成功！图片将在稍后自动移动至原目录'
                        });
                        this.fetchAppData();
                    }).catch(error => {
                        this.$message.error(error.msg);
                    });
                });
            } else {
                this.$prompt(
                    `删除后的App及其${this.currSelectApp.totalImages}张图片将在回收站中保留30天，之后将永远删除。请输入App英文名：`,
                    '警告', {
                        confirmButtonClass: 'delete-action-confirm',
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        inputValidator: (val) => {
                            return val === this.currSelectApp.englishName;
                        },
                        inputErrorMessage: 'App英文名不正确'
                    }).then(({value}) => {
                    deleteApp(appKey).then(_ => {
                        this.editDialogVisible = false;
                        this.$message({
                            type: 'success',
                            message: '删除成功！App图片将在稍后自动移动至回收站'
                        });
                        this.fetchAppData();
                    }).catch(error => {
                        this.$message.error(error.msg);
                    });
                });
            }
        },
        clickLockApp(appKey, locked) {
            if (locked) {
                this.$confirm('解锁后App将可以继续上传图片, 是否解锁?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'info'
                }).then(() => {
                    unlockApp(appKey).then(_ => {
                        this.editDialogVisible = false;
                        this.$message({
                            type: 'success',
                            message: '解锁成功!'
                        });
                        this.fetchAppData();
                    }).catch(error => {
                        this.$message.error(error.msg);
                    });
                });
            } else {
                this.$confirm('锁定App后将无法上传图片, 但已上传图片不会受到影响，是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    lockApp(appKey).then(_ => {
                        this.editDialogVisible = false;
                        this.$message({
                            type: 'success',
                            message: '锁定成功!'
                        });
                        this.fetchAppData();
                    }).catch(error => {
                        this.$message.error(error.msg);
                    });
                });
            }
        },
        clickUpdateApp() {
            this.updating = true;
            updateAppInfo(this.currSelectApp.appKey, {
                name: this.currSelectApp.name,
                role: this.currSelectApp.superApp ? 'SUPER' : 'NORMAL'
            }).then(_ => {
                this.editDialogVisible = false;
                this.$message.success("保存成功");
                this.fetchAppData();
            }).catch(error => {
                this.$message.error(error.msg);
            }).finally(() => {
                this.updating = false;
            });
        },
        clickSaveSetting() {
            this.updating = true;
            updateSetting(this.currSelectApp.appKey, {
                namingStrategy: this.settingForm.namingStrategy
            }).then(_ => {
                this.settingDialogVisible = false;
                this.$message.success("保存成功");
                this.fetchAppData();
            }).catch(error => {
                this.$message.error(error.msg);
            }).finally(() => {
                this.updating = false;
            });
        },
        clickGenerateToken() {
            if (this.currSelectApp.hasUploadToken1) {
                this.$confirm('重新生成Token后原Token将失效，是否继续？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(_ => {
                    this.generateTokenActual();
                });
            } else {
                this.generateTokenActual();
            }
        },
        generateTokenActual() {
            generateUploadToken(this.currSelectApp.appKey).then(response => {
                this.settingDialogVisible = false;
                this.tokenValueDialogVisible = true;
                this.tokenValue = response.data;
                this.fetchAppData();
            }).catch(e => {
                this.$message.error(e.msg);
            })
        },
        clickCreateApp() {
            this.updating = true;
            if (this.createAppParam.superApp) {
                this.createAppParam.appRole = 'SUPER';
            } else {
                this.createAppParam.appRole = 'NORMAL';
            }
            registerApp(this.createAppParam).then(response => {
                this.$message.success(response.msg);
                this.createDialogVisible = false;
                this.fetchAppData();
            }).catch(e => {
                this.$message.error(e.msg);
            }).finally(_ => {
                this.updating = false;
            })
        }
    },
    created: function () {
        this.fetchAppData();
    }
}