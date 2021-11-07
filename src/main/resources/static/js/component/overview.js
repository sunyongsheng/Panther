const overview = {
    template: `<el-main>
                <el-row :gutter="20">
                    <el-col :span="8">
                        <div class="grid-content bg-teal">
                            <i class="el-icon-receiving overview-prefix-icon"></i>
                            <div class="overview-detail">
                                <div class="overview-detail-title">App总数</div>
                                <div class="overview-detail-value" th:text="11"></div>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="grid-content bg-red">
                            <i class="el-icon-picture-outline-round overview-prefix-icon"></i>
                            <div class="overview-detail">
                                <div class="overview-detail-title">图片数量</div>
                                <div class="overview-detail-value" th:text="${12}"></div>
                            </div>
                        </div>
                    </el-col>
                    <el-col :span="8">
                        <div class="grid-content bg-purple">
                            <i class="el-icon-time overview-prefix-icon"></i>
                            <div class="overview-detail">
                                <div class="overview-detail-title">运行时长</div>
                                <div class="overview-detail-value" th:text="${13}"></div>
                            </div>
                        </div>
                    </el-col>
                </el-row>
            </el-main>`,

}