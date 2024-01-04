<template>
  <fieldset style="width: 445px;">
    <legend>属性配置</legend>
    <forecolorProperty :property-condition="propertyCondition" />
    <bgcolorProperty :property-condition="propertyCondition" />
    <fontFamilyProperty :property-condition="propertyCondition" />
    <fontSizeProperty :property-condition="propertyCondition" />
    <boldProperty :property-condition="propertyCondition" />
    <italicProperty :property-condition="propertyCondition" />
    <underlineProperty :property-condition="propertyCondition" />
    <alignProperty :property-condition="propertyCondition" />
    <valignProperty :property-condition="propertyCondition" />
    <borderProperty :property-condition="propertyCondition" />
    <newValueProperty :property-condition="propertyCondition" />
    <formatProperty :property-condition="propertyCondition" />
    <rowHeightProperty :property-condition="propertyCondition" />
    <colWidthProperty :property-condition="propertyCondition" />
    <pagingProperty :property-condition="propertyCondition" />
    <urlProperty :property-condition="propertyCondition" />
  </fieldset>
</template>

<script>

import ForecolorProperty from './forecolor'
import BgcolorProperty from './bgcolor'
import FontFamilyProperty from './fontFamily'
import FontSizeProperty from './fontSize'
import BoldProperty from './bold'
import ItalicProperty from './italic'
import UnderlineProperty from './underline'
import AlignProperty from './align'
import ValignProperty from './valign'
import BorderProperty from './border'
import NewValueProperty from './newValue'
import FormatProperty from './format'
import RowHeightProperty from './rowHeight'
import ColWidthProperty from './colWidth'
import PagingProperty from './paging'
import UrlProperty from './url'

export default {
  components: {
    forecolorProperty: ForecolorProperty,
    bgcolorProperty: BgcolorProperty,
    fontFamilyProperty: FontFamilyProperty,
    fontSizeProperty: FontSizeProperty,
    boldProperty: BoldProperty,
    italicProperty: ItalicProperty,
    underlineProperty: UnderlineProperty,
    alignProperty: AlignProperty,
    valignProperty: ValignProperty,
    borderProperty: BorderProperty,
    newValueProperty: NewValueProperty,
    formatProperty: FormatProperty,
    rowHeightProperty: RowHeightProperty,
    colWidthProperty: ColWidthProperty,
    pagingProperty: PagingProperty,
    urlProperty: UrlProperty
  },
  props: {
    propertyCondition: {
      type: Object,
      default: () => {
        return {
          cellStyle: {}
        }
      }
    }
  },
  data: function() {
    return {

    }
  },
  mounted: function() {

  },
  methods: {

  }
}
</script>

<style>
</style>
