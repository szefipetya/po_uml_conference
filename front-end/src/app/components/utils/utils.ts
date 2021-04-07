export function uniqId(prefix = '', postfix = ''): string {
  return Math.floor(Date.now() + Math.random() * 1000000).toString();
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
