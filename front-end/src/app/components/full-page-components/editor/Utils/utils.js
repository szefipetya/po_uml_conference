const rkey = () =>
  Math.random()
    .toString(36)
    .substring(8);
const unFocus = function() {
  if (document.selection) {
    document.selection.empty();
  } else {
    window.getSelection().removeAllRanges();
  }
};

function getRelativeCoordinates(event, referenceElement) {
  const position = {
    x: event.pageX,
    y: event.pageY,
  };

  const offset = {
    left: referenceElement.offsetLeft,
    top: referenceElement.offsetTop,
  };

  let reference = referenceElement.offsetParent;

  while (reference) {
    offset.left += reference.offsetLeft;
    offset.top += reference.offsetTop;
    reference = reference.offsetParent;
  }

  return {
    x: position.x - offset.left,
    y: position.y - offset.top,
  };
}

function round(a, mod) {
  if (a % mod < mod / 2) {
    return a - (a % mod);
  }
  return a + (mod - (a % mod));
}
/* Number.prototype.clamp = function(min, max) {
  return Math.min(Math.max(this, min), max);
}; */
let clamp = function(min, max) {
  return Math.min(Math.max(this, min), max);
};
function getWindowWidth() {
  return Math.max(
    document.body.scrollWidth,
    document.documentElement.scrollWidth,
    document.body.offsetWidth,
    document.documentElement.offsetWidth,
    document.documentElement.clientWidth
  );
}

function getWindowHeight() {
  return Math.max(
    document.body.scrollHeight,
    document.documentElement.scrollHeight,
    document.body.offsetHeight,
    document.documentElement.offsetHeight,
    document.documentElement.clientHeight
  );
}

console.log(`Width:  ${getWindowWidth()}`);
console.log(`Height: ${getWindowHeight()}`);
export { rkey, round, unFocus,clamp, getRelativeCoordinates };
