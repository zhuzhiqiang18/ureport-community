

export function getHeight(elementId) {
  const ele = document.getElementById(elementId)
  const height = document.documentElement.clientHeight
  const top = ele.getBoundingClientRect().top
  return height - top
}
