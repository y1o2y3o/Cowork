<template>
  <el-main>
    <DocTable
      :docData="docData"
      :tableHeader="tableHeader"
      :searchDoc="searchDoc"
      :showElement="showElement"
      :loadPage="loadPage"
      :createDoc="createDoc"
      :uploadDoc="uploadDoc"
      :onRowClick="onRowClick"
      :onRowStarClick="onRowStarClick"
      :onRowDeleteClick="onRowDeleteClick"
      :onRowRecoveryClick="onRowRecoveryClick"
    />
    <el-pagination
      v-show="showPage"
      style="text-align: center"
      background
      :page-size="10"
      layout="prev, pager, next"
      :total="pageSize"
      @current-change="goPage"
      @prev-click="prevPage()"
      @next-click="nextPage()"
    >
    </el-pagination>
  </el-main>
</template>

<script>
import docMixin from '../mixins/doc'

export default {
  mixins: [docMixin],
  data() {
    return {}
  },
  methods: {
    personalDoc() {
      this.docData = []
      this.tableHeader = ''
      Object.assign(this.showElement, {
        search: true,
        create: true,
        remove: true,
        star: true,
        share: true,
      })
      this.loadPage = () => {
        this.getDocData('/doc/count', `/doc?p=${this.currentPage}`)
        this.showPage = true
      }
      this.searchDoc = (title) => {
        this.searchDocs('/doc/search', title)
      }
      this.createDoc = (title) => {
        this.createNewDoc('/doc', title)
      }
      this.uploadDoc = () => {
        this.uploadDocs('/doc/upload')
      }
      this.onRowClick = (data) => {
        this.$router.push({ path: `/doc/${data.id}` })
      }
      this.onRowStarClick = (id) => {
        this.updateDocStar(id)
      }
      this.onRowDeleteClick = (data) => {
        this.deleteDoc('/doc/', data)
      }
      this.loadPage()
    },
  },
  created() {
    this.personalDoc()
  },
}
</script>

<style>
</style>