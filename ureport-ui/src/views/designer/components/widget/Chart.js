export function builder(data) {
  if (data.type === 'histogram' || data.type === 'bar' || data.type === 'line' || data.type === 'area') {
    return builderHistogram(data)
  }
  if (data.type === 'pie' || data.type === 'doughnut') {
    return builderPie(data)
  }
  if (data.type === 'radar') {
    return builderRadar(data)
  }
  if (data.type === 'scatter' || data.type === 'bubble') {
    return builderScatter(data)
  }
  if (data.type === 'combo') {
    return builderCombo(data)
  }
}

function builderHistogram(chart) {
  let type = 'bar'
  if(chart.type === 'line' || chart.type === 'area') {
    type = 'line'
  }
  const {options, dimensions, dataset }  = chart
  const {title, grid, xAxis, yAxis, legend, label }  = options
  const chartOptions = {
    dataset: {
      dimensions: dimensions || [],
      source: dataset || []
    },
    color: options.global.color,
    title: setTitle(title),
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: setLegend(legend),
    xAxis: setAxis(xAxis),
    yAxis: setAxis(yAxis),
    grid: setGrid(grid),
    series: [],
  }
  for(var i = 1; i < dimensions.length; i++) {
    const item = {
      type: type,
      barWidth: options.global.barWidth || 'auto',
      label: setLabel(label),
    }
    if (options.global.type === 'stacked') {
      item.stack = 'total'
    }
    if (options.global.areaStyle) {
      item.areaStyle = {
        opacity: options.global.areaStyle.opacity || 0
      }
    }
    chartOptions.series.push(item)
  }
  return chartOptions
}

function builderPie(chart) {
  const type = chart.type || ''
  const {options, dimensions, dataset }  = chart
  const {title, legend, label }  = options
  const valueField = dimensions[1]
  const chartOptions = {
    dataset: {
      dimensions: dimensions || [],
      source: dataset || []
    },
    color: options.global.color,
    title: setTitle(title),
    tooltip: {
      trigger: 'item',
      formatter: function(param) {
        return `${param.marker}<span class="chart-tooltip-label">${param.name}</span><span class="chart-tooltip-value">${param.data[valueField]}(${param.percent}%)</span>`
      }
    },
    legend: setLegend(legend),
    series: [{
      type: 'pie',
      center:[`${options.global.offsetX || 0}%`,`${options.global.offsetY || 0}%`],
      radius: [`${options.global.insideWidth || 0}%`, `${options.global.outsideWidth || 0}%`],
      label: setLabel(label)
    }]
  }
  return chartOptions
}

function builderRadar(chart) {
  const type = chart.type || ''
  const {options, dimensions, dataset }  = chart
  const {title, grid, legend, label }  = options
  const [legendTop, legendLeft] = legend.position.split('-')
  const chartOptions = {
    color: options.global.color,
    title: setTitle(title),
    legend: setLegend(legend),
    tooltip: {
      confine: true,
      trigger: 'item'
    },
    radar: {
      center:[`${options.global.offsetX || 0}%`,`${options.global.offsetY || 0}%`],
      radius: `${options.global.outsideWidth || 0}%`,
      indicator: [],
      name: {
        textStyle: {
          padding: [-10, -12]
        }
      }
    },
    series: [{
      type: 'radar',
      label:setLabel(label),
      data: []
    }]
  }
  if (options.global.areaStyle.show){
    chartOptions.series[0].areaStyle = {
      opacity: options.global.areaStyle.opacity || 0
    }
  }
  const indicator = chartOptions.radar.indicator
  const seriesData = chartOptions.series[0].data
  for(var j = 1; j < dimensions.length; j++) {
    seriesData.push({
      value: [],
      name: dimensions[j]
    })
  }
  let max = 0
  for (const item of dataset) {
    for(var j = 1; j < dimensions.length; j++) {
      const seriesKey = dimensions[j]
      const val = item[seriesKey]
      if(max < val) {
        max = val
      }
    }
  }
  for (const item of dataset) {
    const key = dimensions[0]
    indicator.push({
      text: item[key],
      max: options.global.max || max,
    })
    for(var j = 1; j < dimensions.length; j++) {
      const series = seriesData[j-1]
      const seriesKey = dimensions[j]
      series.value.push(item[seriesKey])
    }
  }
  return chartOptions
}


function builderScatter(chart) {
  const {options, dimensions, dataset }  = chart
  const {title, grid, xAxis, yAxis, legend, label }  = options
  const [legendTop, legendLeft] = legend.position.split('-')
  const chartOptions = {
    dataset: {
      dimensions: [dimensions[1],dimensions[2],dimensions[0]],
      source: dataset || []
    },
    color: options.global.color,
    title: setTitle(title),
    legend: setLegend(legend),
    grid: setGrid(grid),
    tooltip: {
      trigger: 'item',
      appendToBody: true,
      formatter: function(param) {
        let content = '<div style="font-size:14px;color:#666;font-weight:400;line-height:1;">' + param.data.category + '</div>'
        content = content + '<div style="margin: 10px 0 0;">'
        for(const key in param.data) {
          if(key !== 'category') {
            content = content + '<div style="margin: 5px 0 0;">'
            content = content + param.marker
            content = content + '<span style="font-size:14px;color:#666;font-weight:400;margin-left:2px">' + key + '</span>'
            content = content + '<span style="float:right;margin-left:20px;font-size:14px;color:#666;font-weight:900">' + param.data[key] + '</span>'
            content = content + '</div>'
          }
        }
        content = content + '</div>'
        return content
      }
    },
    xAxis: setAxis(xAxis),
    yAxis: setAxis(yAxis),
    series: [{
      type: 'scatter',
      label:setLabel(label),
    }],
  }
  if(chart.type === 'bubble') {
    var min = 0
    var max = 0
    const key = dimensions[3]
    for(var k = 0; k < dataset.length;k++) {
        const value = dataset[k]
        if(k === 0) {
          min = value[key]
          max = value[key]
        }
        min = Math.min(min,value[key])
        max = Math.max(max,value[key])
    }
    const item = chartOptions.series[0]
    item.symbolSize = function (data) {
      if(min === max){
        return 10
      }
      return ((data[key] - min) * (50 - 5)) / (max - min) + 5
    }
  }
  return chartOptions
}

function builderCombo(chart) {
  const {options, dimensions, dataset }  = chart
  const {title, grid, xAxis, yAxis, yAxis1, legend, label }  = options
  const [legendTop, legendLeft] = legend.position.split('-')
  const chartOptions = {
    dataset: {
      dimensions: dimensions || [],
      source: dataset || []
    },
    color: options.global.color,
    title: setTitle(title),
    tooltip: {
      trigger: 'axis',
      axisPointer: {
        type: 'shadow'
      }
    },
    legend: setLegend(legend),
    grid: setGrid(grid),
    xAxis: setAxis(xAxis),
    yAxis:[setAxis(yAxis),setAxis(yAxis1)],
    series: []
  }
  const series = chartOptions.series
  for(const s of chart.series) {
    const item = {
      type: s.type ? s.type.split("-")[0] : 'bar',
      yAxisIndex: 0,
      label: setLabel(label),
    }
    if(s.yAxisIndex) {
      item.yAxisIndex = 1
    }
    if(s.type && s.type === 'bar-stack') {
      item.stack = 'total'
    }
    series.push(item)
  }
  return chartOptions
}

function setTitle(title) {
  return {
    show: title.show,
    left: title.left,
    text: title.text,
    textStyle: {
      color: title.textStyle.color,
      fontWeight: title.textStyle.fontWeight,
      fontSize: title.textStyle.fontSize
    }
  }
}

function setLegend(legend) {
  const [legendTop, legendLeft] = legend.position.split('-')
  return {
    show: legend.show,
    top: legendTop,
    left: legendLeft,
    orient: legend.orient,
    textStyle: {
      color: legend.textStyle.color,
      fontWeight: legend.textStyle.fontWeight,
      fontSize: legend.textStyle.fontSize
    }
  }
}

function setGrid(grid) {
  return {
    left: grid.left || 0,
    right: grid.right || 0,
    bottom: grid.bottom || 0,
    top: grid.top || 0,
    containLabel: true
  }
}

function setAxis(axis) {
  return {
    show: axis.show,
    type: axis.type,
    boundaryGap: axis.boundaryGap,
    name: axis.title.show ? axis.title.name : '',
    nameLocation: axis.title.location,
    nameRotate: axis.title.rotate || 0,
    nameGap: axis.title.offset || 0,
    nameTextStyle: {
      fontSize: axis.title.textStyle.fontSize,
      color: axis.title.textStyle.color,
      fontWeight: axis.title.textStyle.fontWeight
    },
    axisLine: {
      show: axis.axisLine.show,
      lineStyle: {
        type: axis.axisLine.lineStyle.type,
        width: axis.axisLine.lineStyle.width || 0,
        color: axis.axisLine.lineStyle.color,
      },
    },
    axisTick: {
      show: axis.axisTick.show,
      lineStyle: {
        type: axis.axisTick.lineStyle.type,
        width: axis.axisTick.lineStyle.width || 0,
        color: axis.axisTick.lineStyle.color,
      },
      alignWithLabel: true,
    },
    axisLabel: {
      show: axis.axisLabel.show,
      rotate: axis.axisLabel.rotate || 0,
      margin: axis.axisLabel.margin || 0,
      align: axis.axisLabel.align,
      textStyle: {
        fontSize: axis.axisLabel.textStyle.fontSize,
        color: axis.axisLabel.textStyle.color,
        fontWeight: axis.axisLabel.textStyle.fontWeight
      },
    },
    splitLine: {
      show: axis.splitLine.show,
      lineStyle: {
        type: axis.splitLine.lineStyle.type,
        width: axis.splitLine.lineStyle.width || 0,
        color: axis.splitLine.lineStyle.color
      }
    }
  }
}

function setLabel(label) {
  return {
    show: label.show,
    position: label.position,
    textStyle: {
      fontSize: label.textStyle.fontSize,
      color: label.textStyle.color,
      fontWeight: label.textStyle.fontWeight
    },
    offset: [label.offsetX, label.offsetY],
  }
}
