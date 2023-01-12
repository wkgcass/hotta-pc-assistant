var fs = require('fs');
var buf = fs.readFileSync('./dec');

function encodeMapLow(a) {
  var low = a & 0xf;
  switch (low) {
    case 0x0: return 0x8;
    case 0x1: return 0x7;
    case 0x2: return 0xa;
    case 0x3: return 0x9;
    case 0x4: return 0xc;
    case 0x5: return 0xb;
    case 0x6: return 0xe;
    case 0x7: return 0xd;
    case 0x8: return 0x0;
    case 0x9: return 0xf;
    case 0xa: return 0x2;
    case 0xb: return 0x1;
    case 0xc: return 0x4;
    case 0xd: return 0x3;
    case 0xe: return 0x6;
    case 0xf: return 0x5;
  }
}

function encodeMapHigh(a) {
  var l = a & 0xf;
  var h = (a >> 4) & 0xf;
  if (h <= 0xa) {
    h = 0xa - h;
  } else {
    h = 0xf + 0xb - h;
  }
  if (l >= 0xa || l == 8) {
    if (h == 0xf) h = 0;
    else h += 1;
  }
  return h;
}

function encodeMap(a) {
  var l = encodeMapLow(a);
  var h = encodeMapHigh(a);
  return (h << 4) | l;
}

for (var i = 0; i < buf.length; ++i) {
  var b = buf[i];
  var n = encodeMap(b);
  buf[i] = n;
}

var len = buf.length;
var h0 = len & 0xff;
var h1 = (len >> 8) & 0xff;
var h2 = (len >> 16) & 0xff;
var h3 = (len >> 24) & 0xff;

buf = Buffer.concat([Buffer.from([0xca, 0x9d, 0xae, 0x86, 0xbf, 0xa9, 0xa0, 0xaa, 0x02, 0x00, 0x00, 0x00, h0, h1, h2, h3]), buf]);

fs.writeFileSync('./enc', buf);
