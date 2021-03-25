export function uniqId(prefix = '', postfix = ''): string {
  return Math.floor(Date.now() + Math.random() * 1000000).toString();
}
('use strict');
export function soft_copy(source, target, keys?) {
  for (var i in source) {
    if (keys.indexOf(i) >= 0) continue;
    if (!Object.prototype.hasOwnProperty.call(source, i)) continue;
    target[i] = source[i];
  }
  return target;
}
