<template>
  <div class="component-icon-btn" @click="handleUndo">
    <div class="component-icon-btn-icon">
      <i class="icons icons-16 icons-16-undo"></i>
    </div>
  </div>
</template>

<script>
import {
  undoManager
} from '../Utils.js'

export default {
  props: {
    context: {
      type: Object,
      required: true
    }
  },
  data: function() {
    return {

    }
  },
  mounted: function() {

  },
  methods: {
    handleUndo() {
      if (undoManager.hasUndo()) {
        undoManager.undo()
      } else {
        this.$message('当前没有内容可以撤销')
      }
    }
  }
}
</script>
