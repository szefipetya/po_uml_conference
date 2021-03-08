import { Injectable } from '@angular/core';

import { LINE_HEAD } from '../../../../models/line/LINE_HEAD';
@Injectable({
  providedIn: 'root',
})
export class ResourceLoaderService {
  public getSvgHead(type: LINE_HEAD): any {
    let img = new Image();

    switch (type) {
      case LINE_HEAD.NONE:
        break;
      case LINE_HEAD.ARROW:
        img.src = './../../../../../../assets/arrow.svg';
        break;
      case LINE_HEAD.RHOMBUS_EMPTY:
        img.src = './../../../../../../assets/empty_rhombus.svg';
        break;
      case LINE_HEAD.RHOMBUS_FILLED:
        img.src = './../../../../../../assets/filled_rhombus.svg';
        break;
      case LINE_HEAD.TRI_ARROW_EMPTY:
        img.src = './../../../../../../assets/tri_arrow_empty.svg';
        break;
      case LINE_HEAD.TRI_ARROW_FILLED:
        img.src = './../../../../../../assets/tri_arrow_filled.svg';

        break;
    }
    console.dir(img);
    return img;
  }
}
