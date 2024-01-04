
export const Config = {
  global: {
    type: 'group',
    barWidth: 0,
    color: ['rgb(84,112,198)', 'rgb(145,204,117)', 'rgb(250,200,88)', 'rgb(238,102,102)', 'rgb(115,192,222)', 'rgb(59,162,114)', 'rgb(252,132,82)', 'rgb(154,96,180)'],
  },
  title: {
    show: false,
    left: 'left',
    text: '标题文本',
    textStyle: {
      color: '#595959',
      fontWeight: 'bold',
      fontSize: 16
    }
  },
  label: {
    show: false,
    position: 'top',
    textStyle: {
      fontSize: 12,
      color: '#606266',
      fontWeight: 'normal',
    },
    offsetX: 0,
    offsetY: 0,
  },
  grid: {
    show: true,
    left: 10,
    right: 10,
    bottom: 10,
    top: 32,
    containLabel: true
  },
  xAxis: {
    show: true,
    type: 'category',
    extent: {
      min: 'auto',
      max: 'auto',
    },
    boundaryGap: true,
    title: {
      show: false,
      name: 'X轴',
      location: 'center',
      rotate: 0,
      offset: 20,
      textStyle: {
        fontSize: 12,
        color: '#909399',
        fontWeight: 'normal',
      },
    },
    axisLine: {
      show: true,
      lineStyle: {
        type: 'solid',
        width: 1,
        color: '#E4E7ED',
      }
    },
    axisTick: {
      show: false,
      lineStyle: {
        type: 'solid',
        width: 1,
        color: '#E4E7ED',
      }
    },
    axisLabel: {
      show: true,
      interval: 'auto',
      rotate: 0,
      margin: 10,
      align: 'center',
      textStyle: {
        fontSize: 12,
        color: '#606266',
        fontWeight: 'normal',
      },
    },
    splitLine: {
      show: false,
      lineStyle: {
        type: 'solid',
        width: 1,
        color: '#E4E7ED',
      }
    },
  },
  yAxis: {
    show: true,
    type: 'value',
    extent: {
      min: 'auto',
      max: 'auto',
    },
    boundaryGap: false,
    title: {
      show: false,
      name: 'Y轴',
      location: 'center',
      rotate: 90,
      offset: 20,
      textStyle: {
        fontSize: 12,
        color: '#606266',
        fontWeight: 'normal',
      },
    },
    axisLine: {
      show: false,
      lineStyle: {
        type: 'solid',
        width: 1,
        color: '#E4E7ED',
      }
    },
    axisTick: {
      show: false,
      lineStyle: {
        type: 'solid',
        width: 1,
        color: '#E4E7ED',
      }
    },
    axisLabel: {
      show: true,
      boundaryGap: 0,
      rotate: 0,
      margin: 8,
      textStyle: {
        fontSize: 12,
        color: '#606266',
        fontWeight: 'normal',
      },
    },
    splitLine: {
      show: true,
      lineStyle: {
        type: 'solid',
        width: 1,
        color: '#E4E7ED',
      }
    },
  },
  legend: {
    show: true,
    position: 'top-center',
    orient: 'horizontal',
    textStyle: {
      fontSize: 12,
      color: '#606266',
      fontWeight: 'normal',
    }
  }
}
