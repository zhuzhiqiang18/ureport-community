<template>
  <div class="component-icon-btn" @click="handleRedo">
    <div class="component-icon-btn-icon">
      <i class="icons icons-16 icons-16-redo"></i>
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
    handleRedo() {
      if (undoManager.hasRedo()) {
        undoManager.redo()
      } else {
        this.$message('当前没有内容可以恢复')
      }
    }
  }

}
</script>
