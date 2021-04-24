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
function setCookie(name, value, days) {
  var expires = "";
  if (days) {
    var date = new Date();
    date.setTime(date.getTime() + (days * 24 * 60 * 60 * 1000));
    expires = "; expires=" + date.toUTCString();
  }
  document.cookie = name + "=" + (value || "") + expires + "; path=/";
}
function getCookie(name) {
  var nameEQ = name + "=";
  var ca = document.cookie.split(';');
  for (var i = 0; i < ca.length; i++) {
    var c = ca[i];
    while (c.charAt(0) == ' ') c = c.substring(1, c.length);
    if (c.indexOf(nameEQ) == 0) return c.substring(nameEQ.length, c.length);
  }
  return null;
}
function eraseCookie(name) {
  document.cookie = name + '=; Path=/; Expires=Thu, 01 Jan 1970 00:00:01 GMT;';
}
export function uniqId(): string {
  return getRandomInt(-2147483647, 2147483647);
}
function getRandomInt(min, max) {
  min = Math.ceil(min);
  max = Math.floor(max);
  return Math.floor(Math.random() * (max - min + 1)) + min;
}
/*('use strict');
export function soft_copy(source, target, keys) {
  for (let k in source) {
    if (typeof source[k] === 'object') {
      // Array.isArray(source[k])

      //if (!keys.includes(k) && source[k] != null) target[k] = source[k];
      if (target[k] === undefined) target[k] = source[k];
      if (source[k] && keys.includes(k)) {
        target[k] = null;
      }
      if (k && source[k] && target[k])
        if (!keys.includes(k) && source[k] != null) {
          // target[k] = source[k];
          soft_copy(source[k], target[k], keys);
        }
    } else {
      // base case, stop recurring
      if (!keys.includes(k) && source[k] != null) {
        target[k] = source[k];
      }
    }
  }
  return target;
}*/
export function soft_copy(obj, target, keys) {
  for (var i in obj) {
    if (keys.indexOf(i) >= 0) continue;
    if (!Object.prototype.hasOwnProperty.call(obj, i)) continue;
    target[i] = obj[i];
  }
  return target;
}

export { round, unFocus, clamp, getRelativeCoordinates, clone, clone1 };
