<template>
  <el-dialog
    :visible.sync="groupUserDialog"
    @open="getGroupInfo()"
    width="40%"
    :show-close="false"
    :close-on-press-escape="false"
    :close-on-click-modal="false"
  >
    <div slot="title">
      <div class="header-left">
        <span style="font-size: 18px">
          {{ groupInfo ? groupInfo.name : '未加入群组' }}</span
        >
        <el-popover
          style="margin-top: 20px"
          v-if="groupInfo"
          width="260"
          v-model="groupInfoPop"
        >
          <h3 style="text-align: center">{{ groupInfo.name }}</h3>
          <p>
            群介绍：{{ groupInfo.describe ? groupInfo.describe : '暂无介绍' }}
          </p>
          <p>用户数：{{ groupInfo.user }}</p>
          <p>文档数：{{ groupInfo.doc }}</p>
          <p>成员权限：{{ memberRole[groupInfo.memberRole] }}</p>
          <div style="text-align: right; margin: 0">
            <el-button
              size="mini"
              type="danger"
              @click="
                groupInfoPop = false
                exitGroup()
              "
              >退出</el-button
            >
          </div>
          <el-button
            style="margin-left: 20px"
            slot="reference"
            type="text"
            icon="el-icon-more-outline"
          ></el-button>
        </el-popover>
      </div>
      <div class="header-right">
        <span class="header-btn">
          <el-button
            v-show="groupInfo ? userInfo.id === groupInfo.leader_id : false"
            circle
            size="small"
            icon="el-icon-s-operation"
            @click="
              innerDialog = !innerDialog
              updateGroup = true
            "
          ></el-button>
        </span>
        <span class="header-btn">
          <el-badge
            v-show="groupInfo ? userInfo.id === groupInfo.leader_id : false"
            :value="applySize"
            :hidden="applySize === 0"
          >
            <el-button
              icon="el-icon-bell"
              size="small"
              circle
              @click="
                innerDialog = !innerDialog
                updateGroup = false
              "
            ></el-button>
          </el-badge>
        </span>
        <span class="header-btn">
          <el-button
            circle
            size="small"
            icon="el-icon-close"
            @click="closeDialog()"
          ></el-button>
        </span>
      </div>
    </div>
    <el-table
      :data="groupUser"
      height="300"
      style="width: 100%"
      :show-header="false"
    >
      <el-table-column width="120">
        <template slot-scope="scope">
          <el-avatar :src="scope.row.avatar || ''">
            {{ scope.row.username || '' }}
          </el-avatar>
          <el-tag v-if="scope.row.id === groupInfo.leader_id" size="mini"
            >Leader</el-tag
          >
        </template>
      </el-table-column>
      <el-table-column width="150">
        <template slot-scope="scope">
          <span v-show="scope.row.nickname">{{ scope.row.nickname }}</span>
          <br />
          <span>{{ scope.row.username }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="email"> </el-table-column>
      <el-table-column
        v-if="groupInfo ? userInfo.id === groupInfo.leader_id : false"
      >
        <template slot-scope="scope">
          <el-popconfirm
            placement="bottom"
            trigger="manual"
            title="是否将此用户移出本群组？"
            v-model="removePop"
            @confirm="removeUser(scope.row.id)"
          >
            <el-button
              v-if="groupInfo ? scope.row.id !== groupInfo.leader_id : false"
              slot="reference"
              icon="el-icon-delete"
              circle
              size="mini"
              @click.stop="removePop = !removePop"
            ></el-button>
          </el-popconfirm>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      style="text-align: center"
      background
      :page-size="10"
      layout="prev, pager, next"
      :total="groupUserSize"
      @current-change="goPage"
      @prev-click="prevPage()"
      @next-click="nextPage()"
    >
    </el-pagination>
    <el-dialog
      :visible.sync="innerDialog"
      width="40%"
      :title="updateGroup ? '更新群组信息' : '申请消息'"
      append-to-body
    >
      <el-form v-show="updateGroup" :model="groupInfo" label-width="80px">
        <el-form-item label="群组名称">
          <el-input v-model="groupInfo.name"></el-input>
        </el-form-item>
        <el-form-item label="群组介绍">
          <el-input v-model="groupInfo.describe"></el-input>
        </el-form-item>
        <el-form-item label="成员权限">
          <el-select
            v-model="groupInfo.memberRole"
            placeholder="请选择成员权限"
          >
            <el-option
              v-for="(v, k) in memberRole"
              :key="k"
              :label="v"
              :value="k"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="putGroup">更新</el-button>
          <el-button @click="innerDialog = false">取消</el-button>
        </el-form-item>
      </el-form>
      <el-table
        v-show="!updateGroup"
        :data="applyUser"
        height="300"
        style="width: 100%"
        :show-header="false"
      >
        <el-table-column property="username" width="120"> </el-table-column>
        <el-table-column property="nickname" width="150"> </el-table-column>
        <el-table-column property="email"> </el-table-column>
        <el-table-column>
          <template slot-scope="scope">
            <el-popconfirm
              placement="bottom"
              trigger="manual"
              title="是否同意该申请？"
              v-model="applyUserPop"
              @confirm="allowApply(scope.row.id)"
            >
              <el-button
                slot="reference"
                icon="el-icon-check"
                circle
                size="mini"
                @click.stop="applyUserPop = !applyUserPop"
              ></el-button>
            </el-popconfirm>
            <el-popconfirm
              style="margin-left: 10px"
              placement="bottom"
              trigger="manual"
              title="是否拒绝该申请？"
              v-model="denyGroupPop"
              @confirm="denyApply(scope.row.id)"
            >
              <el-button
                slot="reference"
                icon="el-icon-close"
                circle
                size="mini"
                @click.stop="denyGroupPop = !denyGroupPop"
              ></el-button>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </el-dialog>
</template>

<script>
import { mapState } from 'vuex'
export default {
  props: {
    groupUserDialog: {
      type: Boolean,
      default: false,
    },
    gid: {
      type: String,
    },
  },
  data() {
    return {
      groupInfo: {},
      applyUserPop: false,
      innerDialog: false,
      denyGroupPop: false,
      groupUserSize: this.groupInfo ? this.groupInfo.user : 0,
      currentPage: 0,
      groupInfoPop: false,
      groupUser: [],
      applyUser: [],
      applySize: 0,
      removePop: false,
      updateGroup: false,
      putGroupInfo: {
        name: '',
        describe: '',
        memberRole: 'Create',
      },
      memberRole: {
        ReadWrite: '读写群组文档',
        Create: '读写、创建群组文档',
        CreateDelete: '读写、创建、删除群组文档',
      },
    }
  },
  methods: {
    closeDialog() {
      this.$emit('update:groupUserDialog', false)
    },
    getGroupInfo() {
      this.$axios
        .get(`/group/${this.currentGroup}`)
        .then((res) => {
          if (res.data.code == 200) {
            this.groupInfo = res.data.data
            this.getGroupUser()
          } else this.$message.error(res.data.msg)
        })
        .catch(() => {})
    },
    getGroupUser() {
      this.$axios
        .get(`/group/${this.currentGroup}/user?p=${this.currentPage}`)
        .then((res) => {
          if (res.data.code == 200) {
            this.groupUser = res.data.data
            if (
              this.groupInfo &&
              this.userInfo.id === this.groupInfo.leader_id
            ) {
              this.getApplyUser()
            }
          }
        })
        .catch(() => {})
    },
    getApplyUser() {
      this.$axios
        .get(`/group/${this.currentGroup}/apply`)
        .then((res) => {
          if (res.data.code == 200) {
            this.applyUser = res.data.data
            this.applySize = this.applyUser.length
          } else this.$message.error(res.data.msg)
        })
        .catch(() => {})
    },
    allowApply(id) {
      this.$axios
        .post(`/group/${this.currentGroup}/apply/${id}`)
        .then((res) => {
          if (res.data.code == 200) {
            this.$message.success(res.data.msg)
            this.getApplyUser()
            this.getGroupUser()
          } else this.$message.error(res.data.msg)
        })
    },
    denyApply(id) {
      this.$axios
        .delete(`/group/${this.currentGroup}/apply/${id}`)
        .then((res) => {
          if (res.data.code == 200) {
            this.$message.success(res.data.msg)
            this.getApplyUser()
          } else this.$message.error(res.data.msg)
        })
    },
    removeUser(id) {
      this.$axios
        .delete(`/group/${this.currentGroup}/user/${id}`)
        .then((res) => {
          if (res.data.code == 200) {
            this.$message.success(res.data.msg)
            this.getGroupUser()
          } else this.$message.error(res.data.msg)
        })
        .catch(() => {})
    },
    exitGroup() {
      this.$axios
        .delete(`/group/${this.currentGroup}/exit`)
        .then((res) => {
          if (res.data.code == 200) {
            this.$message.success(res.data.msg)
          } else this.$message.error(res.data.msg)
        })
        .catch(() => {})
    },
    putGroup() {
      this.$axios
        .put(
          `/group/${this.currentGroup}`,
          new URLSearchParams({
            name: this.groupInfo.name,
            describe: this.groupInfo.describe,
            memberRole: this.groupInfo.memberRole,
          })
        )
        .then((res) => {
          if (res.data.code == 200) {
            this.$message.success(res.data.msg)
          } else this.$message.error(res.data.msg)
        })
        .catch(() => {})
    },
    goPage(page) {
      this.currentPage = page - 1
      this.getGroupUser()
    },
    prevPage() {
      if (this.currentPage > 1) this.currentPage--
      this.getGroupUser()
    },
    nextPage() {
      this.currentPage++
      this.getGroupUser()
    },
  },
  computed: {
    ...mapState({
      userInfo: (state) => state.userInfo || {},
      currentGroup: (state) => state.currentGroup,
    }),
  },
}
</script>

<style>
.header-left {
  width: 50%;
  float: left;
  height: 100%;
  padding-top: 10px;
}
.header-right {
  width: 50%;
  text-align: right;
  overflow: hidden;
  height: 100%;
  padding-top: 10px;
}
.header-btn {
  margin-left: 10px;
  vertical-align: top;
}
</style>