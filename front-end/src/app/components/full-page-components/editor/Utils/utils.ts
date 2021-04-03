const unFocus = function () {
  if (document.getSelection()) {
    document.getSelection().empty();
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
let clamp = function (min, max) {
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

function clone(obj) {
  if (null == obj || 'object' != typeof obj) return obj;
  var copy = obj.constructor();
  for (var attr in obj) {
    if (obj.hasOwnProperty(attr)) copy[attr] = obj[attr];
  }
  return copy;
}
function clone1(obj, param1) {
  if (null == obj || 'object' != typeof obj) return obj;
  let copy = new obj.constructor(param1);
  for (var attr in obj) {
    if (obj.hasOwnProperty(attr)) copy[attr] = obj[attr];
  }
  return copy;
}

console.log(`Width:  ${getWindowWidth()}`);
console.log(`Height: ${getWindowHeight()}`);

export class Pair<K, V> {
  key: K;
  value: V;
  constructor(key: K, value: V) {
    this.key = key;
    this.value = value;
  }
}
export { round, unFocus, clamp, getRelativeCoordinates, clone, clone1 };
