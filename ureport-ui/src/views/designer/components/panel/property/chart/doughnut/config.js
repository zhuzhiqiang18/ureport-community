
export const Config = {
  global: {
    color: ['rgb(84,112,198)', 'rgb(145,204,117)', 'rgb(250,200,88)', 'rgb(238,102,102)', 'rgb(115,192,222)', 'rgb(59,162,114)', 'rgb(252,132,82)', 'rgb(154,96,180)'],
    outsideWidth: 70,
    insideWidth: 40,
    offsetX: 50,
    offsetY: 50,
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
    show: true,
    position: 'inside',
    textStyle: {
      fontSize: 12,
      color: '#fff',
      fontWeight: 'normal',
    },
    offsetX: 0,
    offsetY: 0,
  },
  grid: {
    show: true,
    left: 10,
    right: 32,
    bottom: 10,
    top: 32,
    containLabel: true
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
