<template>
  <el-autocomplete
    v-model="option"
    :fetch-suggestions="search"
    :placeholder="placeholder"
    style="width:100%"
    @change="handleChange"
    @select="handleSelect"
  />
</template>

<script>
export default {
  model: {
    prop: 'value',
    event: 'change'
  },
  props: {
    value: {
      type: String,
      default: () => {
        return ''
      }
    },
    placeholder:{
      type: String,
      default: () => {
        return '对数字和日期格式化'
      }
    },
    data: {
      type: Array,
      default: () => {
        return []
      }
    }
  },
  data() {
    return {
      option: ''
    }
  },
  watch: {
    value: function(val) {
      this.option = val
    }
  },
  mounted() {
    this.option = this.value
  },
  methods: {
    search(str, cb) {
      const restaurants = this.data
      let results = restaurants.filter(function(ele) {
        return ele.value.toLowerCase().indexOf(str.toLowerCase()) > -1
      })
      cb(results)
    },
    handleChange(val) {
      this.option = val
      this.$emit('change', val)
    },
    handleSelect(val) {
      this.option = val.value
      this.$emit('change', val.value)
    },
  }
}
</script>

<style>
</style>
