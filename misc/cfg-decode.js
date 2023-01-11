var fs = require('fs');
var buf = fs.readFileSync('./some.cfg');
buf = buf.slice(16);

function decodeMapLow(a) {
  var low = a & 0xf;
  switch (low) {
    case 0x8: return 0x0;
    case 0x7: return 0x1;
    case 0xa: return 0x2;
    case 0x9: return 0x3;
    case 0xc: return 0x4;
    case 0xb: return 0x5;
    case 0xe: return 0x6;
    case 0xd: return 0x7;
    case 0x0: return 0x8;
    case 0xf: return 0x9;
    case 0x2: return 0xa;
    case 0x1: return 0xb;
    case 0x4: return 0xc;
    case 0x3: return 0xd;
    case 0x6: return 0xe;
    case 0x5: return 0xf;
  }
}

function decodeMapHigh(a) {
  var h = (a >> 4) & 0xf;
  var l = decodeMapLow(a);
  if (l >= 0xa || l == 8) {
    if (h == 0) h = 0xf;
    else h -= 1;
  }
  if (h <= 0xa) {
    h = 0xa - h;
  } else {
    h = 0xf + 0xb - h;
  }
  return h;
}

function decodeMap(a) {
  var l = decodeMapLow(a);
  var h = decodeMapHigh(a);
  return (h << 4) | l;
}

for (var i = 0; i < buf.length; ++i) {
  var b = buf[i];
  var n = decodeMap(b);
  buf[i] = n;
}

fs.writeFileSync('./dec', buf);
