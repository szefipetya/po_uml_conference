import { Component, OnInit, ViewChild } from '@angular/core';
import { SimpleClass } from 'src/app/components/models/DiagramObjects/SimpleClass';
import { GlobalEditorService } from '../services/global-editor/global-editor.service';
import { round, unFocus } from '../Utils/utils';
import { LineCanvasComponent } from './line-canvas/line-canvas.component';
import { GROUP_SYNTAX } from 'src/app/components/models/DiagramObjects/GROUP_SYNTAX';
@Component({
  selector: 'app-canvas-box',
  templateUrl: './canvas-box.component.html',
  styleUrls: ['./canvas-box.component.scss'],
})
export class CanvasBoxComponent implements OnInit {
  borderString_scale: string;
  targetResizeHoverXR: boolean;
  targetResizeHoverXL: boolean;
  targetResizeHoverYR: boolean;
  targetResizeHoverYL: boolean;
  findClassById = (id) => {
    const cls = this.editorService.model.classes.filter((e) => e.id == id);
    return cls;
  };

  zoom(event) {
    console.log('zoom');
    event.preventDefault();
    let scale = 1;

    scale += event.deltaY * -0.0006;
    // Restrict scale
    //scale = clamp(scale, 0.96, 1.04);
    this.editorService.model.classes.map((e) => {
      e.scaledModel.posx_scaled *= scale;
      e.scaledModel.posy_scaled *= scale;
      e.scaledModel.width_scaled *= scale;
      e.scaledModel.height_scaled *= scale;

      if (e.titleModel.edit) {
        e.titleModel.viewModel.render();
      }
      e.groups.map((group) => {
        group.attributes.map((e2) => {
          if (e2.edit) {
            e2.viewModel.inputWidth *= scale;
            e2.viewModel.render();
          }
        });
      });
    });
    this.editorService.clientModel.class_general.fontsize_scaled *= scale;
    this.editorService.clientModel.class_general.padding_scaled *= scale;
    this.editorService.clientModel.class_general.border_scaled *= scale;
    this.editorService.clientModel.class_general.min_height_scaled *= scale;
    this.editorService.clientModel.class_general.min_width_scaled *= scale;
    this.editorService.clientModel.canvas.scale *= scale;
    this.editorService.clientModel.canvas.posx *= scale;
    this.editorService.clientModel.canvas.posy *= scale;
    this.editorService.clientModel.canvas.width *= scale;
    this.editorService.clientModel.canvas.height *= scale;
    this.editorService.clientModel.canvas.gridSize *= scale;

    this.borderString_scale = `${this.editorService.clientModel.class_general.border_scaled}px gold solid`;
    this.onMouseUp(null);
    this.updateCanvas();
  }

  drawedClassX: number;
  drawedClassY: number;
  getNewClassId = () => {
    const found = false;
    const match = 0;
    let id;
    for (let i = 0; i < this.editorService.model.classes.length - 1; i++) {
      if (
        Math.abs(
          Number.parseInt(this.editorService.model.classes[i].id) -
            Number.parseInt(this.editorService.model.classes[i + 1].id)
        ) >= 2
      ) {
        id =
          Math.min(
            Number.parseInt(this.editorService.model.classes[i].id),
            Number.parseInt(this.editorService.model.classes[i + 1].id)
          ) + 1;
      }
    }
    if (!id) {
      id =
        this.editorService.model.classes[
          this.editorService.model.classes.length - 1
        ].id + 1;
    }
    return id;
  };
  getHighestClassZIndex = () => {
    let max = 0;
    this.editorService.model.classes.map((e) => {
      if (e.z > max) max = e.z;
    });

    if (max > 1000) {
      this.editorService.model.classes.map((e) => {
        e.z--;
      });
    }
    return max + 1;
  };

  xdiff: number;
  ydiff: number;
  clipDOM: any;
  updateClassSelection = () => {
    this.editorService.model.classes.map((clas) => {
      this.findClassDOMbyId(clas.id).classList.remove('d-class-selected');
    });
    this.editorService.clientModel.canvas.selectedClassIds.map((id) => {
      this.findClassDOMbyId(id).classList.add('d-class-selected');
    });
  };
  /** Amikor a felhasználó éppen átméretez,
    mindig pillanatnyi méretezés előtti állapotát menti.
     * */
  setTargets = (e) => {
    let dclass = e.target.closest('.d-class');
    if (!dclass) {
      dclass = e.target.querySelector('.d-class');
    }
    if (dclass) {
      if (e.target.className != 'edit-box' && e.target.nodeName != 'INPUT') {
        this.targetClass = this.findClassById(dclass.id)[0];
        this.targetRect = dclass.parentNode.getBoundingClientRect();
        this.targetInner = dclass.getBoundingClientRect();
      }
      if (this.targetInner != undefined) {
        this.xdiff = e.clientX - this.targetInner.left;
        this.ydiff = e.clientY - this.targetInner.top;
        if (this.targetInner_stored != undefined) {
          this.xdiff_rel_to_stored = e.clientX - this.targetInner_stored.left;
          this.ydiff_rel_to_stored = e.clientY - this.targetInner_stored.top;
        }
        return true;
      }
      return false;
    }
    return false;
  };

  setStoredTargets = (e) => {
    if (this.targetInner != undefined) {
      const dclass = e.target.closest('.d-class');
      if (dclass) {
        this.dclass = dclass;
        this.targetClass_stored = this.findClassById(dclass.id)[0];
        this.targetWidth_stored = this.targetClass_stored.scaledModel.width_scaled;
        this.targetHeight_stored = this.targetClass_stored.scaledModel.height_scaled;
        this.targetRect_stored = dclass.parentNode.getBoundingClientRect();
        this.targetInner_stored = dclass.getBoundingClientRect();

        this.stored_mx = e.clientX - this.targetRect_stored.left;
        this.stored_my = e.clientY - this.targetRect_stored.top;
        this.stored_xdiff = e.clientX - this.targetInner_stored.left;
        this.stored_ydiff = e.clientY - this.targetInner_stored.top;
        this.targetDOM = dclass;
        return true;
      }
    } else {
      return false;
    }
  };
  selectClickedClassOnly = () => {
    this.editorService.clientModel.canvas.selectedClassIds = [];
    if (this.targetClass_stored)
      this.editorService.clientModel.canvas.selectedClassIds.push(
        this.targetClass_stored.id
      );
  };
  resizePadding = 14;
  targetCurrentId = 1;
  targetCorrigateTransition = 300;
  gridSize = 10;

  drawedClassPositionSpecified: boolean;
  targetInner_stored: any;
  xdiff_rel_to_stored: number;
  ydiff_rel_to_stored: number;
  dclass: any;
  targetClass_stored: SimpleClass;
  targetWidth_stored: any;
  targetHeight_stored: any;
  targetRect_stored: any;
  stored_mx: number;
  stored_my: number;
  stored_xdiff: number;
  stored_ydiff: number;

  ebox: any;
  updateCanvas() {
    this.lineCanvasComponent.update();
  }
  targetDOM: any;
  holdingAny: boolean;
  holding: boolean;

  corrigateTargetClassPosition = () => {
    if (this.targetClass_stored != undefined) {
      const canvas = { ...this.editorService.clientModel.canvas };
      let c = 0;
      const {
        posx_scaled,
        posy_scaled,
        width_scaled,
        height_scaled,
      } = this.targetClass_stored.scaledModel;
      if (posx_scaled < 0) {
        this.targetClass_stored.scaledModel.posx_scaled = 0;
        c++;
      }
      if (posy_scaled < 0) {
        this.targetClass_stored.scaledModel.posy_scaled = 0;
        c++;
      }
      if (posx_scaled + width_scaled > canvas.width) {
        this.targetClass_stored.scaledModel.posx_scaled =
          canvas.width - width_scaled;
        c++;
      }
      if (posy_scaled + height_scaled > canvas.height) {
        this.targetClass_stored.scaledModel.posy_scaled =
          canvas.height - height_scaled;
        c++;
      }
      if (c > 0) {
        if (this.targetDOM) {
          this.targetDOM.style.transition = `all ${this.targetCorrigateTransition}ms ease`;
          setTimeout(() => {
            this.targetDOM.style.transition = 'unset';
          }, this.targetCorrigateTransition);
        }
        this.updateCanvas();
      }
    }
  };

  corrigateTargetClassDimensions = () => {
    if (this.targetClass != undefined) {
      if (
        this.targetClass.scaledModel.width_scaled <
        this.editorService.clientModel.class_general.min_width_scaled
      )
        this.targetClass.scaledModel.width_scaled = round(
          this.editorService.clientModel.class_general.min_width_scaled,
          this.editorService.clientModel.canvas.gridSize
        );
      if (
        this.targetClass.scaledModel.height_scaled <
        this.editorService.clientModel.class_general.min_height_scaled
      )
        this.targetClass.scaledModel.height_scaled = round(
          this.editorService.clientModel.class_general.min_height_scaled,
          this.editorService.clientModel.canvas.gridSize
        );

      this.targetClass.groups.map((group) => {
        group.attributes.map((a) => {
          a.viewModel.render();
        });
      });
      this.targetClass.titleModel.viewModel.render();
    }
  };
  targetResizeGrabXR: boolean;
  targetResizeGrabYR: boolean;
  targetResizeGrabXL: boolean;
  targetResizeGrabYL: boolean;
  targetInner: any;
  targetRect: any;
  targetClass: any;
  drawedClassId: string;

  corrigateCanvasPosition = () => {
    const canvas = { ...this.editorService.clientModel.canvas };
    const clip = { ...this.editorService.clientModel.canvas.clip };
    let c = 0;
    if (this.editorService.clientModel.canvas.posx > 0) {
      this.editorService.clientModel.canvas.posx = 0;
      c++;
    }
    if (this.editorService.clientModel.canvas.posy > 0) {
      this.editorService.clientModel.canvas.posy = 0;
      c++;
    }
    if (
      this.editorService.clientModel.canvas.posx +
        this.editorService.clientModel.canvas.width <
      clip.width
    ) {
      this.editorService.clientModel.canvas.posx =
        clip.width - this.editorService.clientModel.canvas.width;
      c++;
    }
    if (
      this.editorService.clientModel.canvas.posy +
        this.editorService.clientModel.canvas.height <
      clip.height
    ) {
      this.editorService.clientModel.canvas.posy =
        clip.height - this.editorService.clientModel.canvas.height;
      c++;
    }
    if (c > 0) {
      this.ebox = document.querySelector('.edit-box');
      this.ebox.style.transition = `all ${this.targetCorrigateTransition}ms ease`;
      setTimeout(() => {
        this.ebox.style.transition = 'unset';
      }, this.targetCorrigateTransition);
      this.updateCanvas();
      /*  this.setState({ canvas }) */
    }
  };
  ngOnInit(): void {}
  editorService: GlobalEditorService;
  constructor(editorService: GlobalEditorService) {
    this.editorService = editorService;
    // this.editorService.clientModel.canvas.viewModel = this;
  }
  onMouseMove = (e) => {
    //console.dir(e);
    // e.persist();
    // ha, éppen class-t rajzol az illető
    if (
      this.drawedClassPositionSpecified &&
      this.editorService.clientModel.canvas.drawMode == 'class'
    ) {
      this.resizeDrawedClass(e);
      return;
    }
    // ha a 'canvas-ra kattintott, és úgy mozgat
    if (!this.dclass || e.target.className == 'edit-box') {
      if (this.repositionCanvas(e)) return;
    }

    if (this.editorService.clientModel.canvas.drawMode == 'line')
      this.lineCanvasComponent.drawMove(e);
    // class újraméretezés//
    else this.resizeExistingClass(e);
  };
  resizeExistingClass = (e) => {
    let nohover = 0;
    if (
      this.holdingAny &&
      !this.targetResizeGrabXR &&
      !this.targetResizeGrabYR &&
      !this.targetResizeGrabXL &&
      !this.targetResizeGrabYL &&
      this.targetClass != undefined
    ) {
      unFocus();
      this.targetClass.scaledModel.posx_scaled =
        e.clientX - this.targetRect.left - this.xdiff;
      this.targetClass.scaledModel.posy_scaled =
        e.clientY - this.targetRect.top - this.ydiff;
      this.targetClass.scaledModel.posx_scaled = round(
        this.targetClass.scaledModel.posx_scaled,
        this.editorService.clientModel.canvas.gridSize
      );
      this.targetClass.scaledModel.posy_scaled = round(
        this.targetClass.scaledModel.posy_scaled,
        this.editorService.clientModel.canvas.gridSize
      );
      this.updateCanvas();
      return;
    }
    // resize xr
    if (
      this.setTargets(e) &&
      this.xdiff >=
        this.targetClass.scaledModel.width_scaled - this.resizePadding &&
      this.xdiff <= this.targetClass.scaledModel.width_scaled
    ) {
      document.getElementById('root').style.cursor = 'ew-resize';
      this.targetResizeHoverXR = true;
    } else {
      nohover++;
    }
    if (this.targetResizeGrabXR && this.targetClass_stored) {
      unFocus();
      this.targetClass_stored.scaledModel.width_scaled =
        this.xdiff_rel_to_stored +
        (this.targetWidth_stored - this.stored_xdiff);
      this.targetDOM.style.borderRight = this.borderString_scale;
      this.updateCanvas();
      // return;
    }
    // resize xl
    if (
      this.setTargets(e) &&
      this.xdiff <= this.resizePadding &&
      this.xdiff >= 0
    ) {
      document.getElementById('root').style.cursor = 'ew-resize';
      this.targetResizeHoverXL = true;
    } else {
      nohover++;
    }
    if (this.targetResizeGrabXL && this.targetClass_stored) {
      unFocus();

      this.targetClass_stored.scaledModel.posx_scaled =
        e.clientX - this.targetRect.left - this.stored_xdiff;
      const mx = e.clientX - this.targetRect.left;
      this.targetClass_stored.scaledModel.width_scaled =
        this.targetWidth_stored - (mx - this.stored_mx);
      this.targetDOM.style.borderLeft = this.borderString_scale;
      this.targetClass_stored.scaledModel.posx_scaled = round(
        this.targetClass_stored.scaledModel.posx_scaled,
        this.editorService.clientModel.canvas.gridSize
      );
    }
    //  }
    // resize yr
    if (
      this.setTargets(e) &&
      this.ydiff >=
        this.targetClass.scaledModel.height_scaled - this.resizePadding &&
      this.ydiff <= this.targetClass.scaledModel.height_scaled
    ) {
      document.getElementById('root').style.cursor = 'ns-resize';
      this.targetResizeHoverYR = true;
    } else {
      nohover++;
    }
    if (this.targetResizeGrabYR && this.targetClass_stored) {
      unFocus();
      this.targetClass_stored.scaledModel.height_scaled =
        this.ydiff_rel_to_stored +
        (this.targetHeight_stored - this.stored_ydiff);
      this.targetDOM.style.borderBottom = this.borderString_scale;
    }

    //  }

    // resize yl
    if (
      this.setTargets(e) &&
      this.ydiff <= this.resizePadding &&
      this.ydiff >= 0
    ) {
      document.getElementById('root').style.cursor = 'ns-resize';
      this.targetResizeHoverYL = true;
    } else {
      nohover++;
    }
    if (this.targetResizeGrabYL && this.targetClass_stored) {
      unFocus();
      this.targetClass_stored.scaledModel.posy_scaled =
        e.clientY - this.targetRect.top - this.stored_ydiff;
      const my = e.clientY - this.targetRect.top;
      this.targetClass_stored.scaledModel.height_scaled =
        this.targetHeight_stored - (my - this.stored_my);
      this.targetDOM.style.borderTop = this.borderString_scale;
      this.targetClass_stored.scaledModel.posy_scaled = round(
        this.targetClass_stored.scaledModel.posy_scaled,
        this.editorService.clientModel.canvas.gridSize
      );
      // return;
    }
    if (nohover == 4) {
      document.getElementById('root').style.cursor = 'default';
    }

    //  }
    if (this.targetResizeHoverXR && this.targetResizeHoverYR) {
      document.getElementById('root').style.cursor = 'nw-resize';
    }
    if (this.targetResizeHoverXR && this.targetResizeHoverYL) {
      document.getElementById('root').style.cursor = 'ne-resize';
    }
    if (this.targetResizeHoverXL && this.targetResizeHoverYR) {
      document.getElementById('root').style.cursor = 'sw-resize';
    }
    if (this.targetResizeHoverXL && this.targetResizeHoverYL) {
      document.getElementById('root').style.cursor = 'nw-resize';
    }
    this.targetResizeHoverXR = false;
    this.targetResizeHoverYR = false;
    this.targetResizeHoverXL = false;
    this.targetResizeHoverYL = false;
    if (this.targetClass != undefined) {
      this.targetClass.scaledModel.width_scaled = round(
        this.targetClass.scaledModel.width_scaled,
        this.editorService.clientModel.canvas.gridSize
      );
      this.targetClass.scaledModel.height_scaled = round(
        this.targetClass.scaledModel.height_scaled,
        this.editorService.clientModel.canvas.gridSize
      );
      this.corrigateTargetClassDimensions();
    }
  };

  repositionCanvas = (e) => {
    if (this.holding) {
      if (this.clipDOM) {
        this.editorService.clientModel.canvas.posx =
          e.clientX - this.clipDOM.getBoundingClientRect().left - this.xdiff;
        this.editorService.clientModel.canvas.posy =
          e.clientY - this.clipDOM.getBoundingClientRect().top - this.ydiff;
        //  this.updateCanvas();
        /* this.setState({ canvas }) */
        return true;
      }
    }
    return false;
  };
  resizeDrawedClass = (e) => {
    const ebox = e.target.closest('.edit-box');
    if (ebox) {
      const rect = ebox.getBoundingClientRect();
      const x = e.clientX - rect.left; // x position within the element.
      const y = e.clientY - rect.top;

      this.findClassById(this.drawedClassId)[0].scaledModel.width_scaled =
        x - this.drawedClassX;
      this.findClassById(this.drawedClassId)[0].scaledModel.height_scaled =
        y - this.drawedClassY;
      unFocus();
      this.updateCanvas();
    }
  };
  findClassDOMbyId(id): any {
    let dom = document.querySelector('.edit-box').querySelector('#' + id);
    //console.log(dom);
    return dom;
  }
  onClick(e) {
    console.log(e);
    if (e.target.className == 'attribute') {
      e.target.click();
    }
  }
  onMouseUp(e) {
    if (this.editorService.clientModel.canvas.drawMode == 'line') {
      this.lineCanvasComponent.drawEnd(e);
    }
    if (this.editorService.clientModel.canvas.drawMode != 'cursor') {
      const drawedclass = this.findClassDOMbyId(this.drawedClassId);
      this.drawedClassPositionSpecified = false;
      this.drawedClassId = undefined;
    }

    if (this.targetDOM != undefined)
      this.targetDOM.style.border = `${this.editorService.clientModel.class_general.border_scaled}px solid rgba(255, 255, 255, 0.19)`;
    this.resetMouseState();
  }

  resetMouseState() {
    this.editorService.clientModel.canvas.drawMode = 'cursor';
    this.holdingAny = false;
    this.holding = false;
    this.corrigateTargetClassPosition();
    this.corrigateTargetClassDimensions();
    this.targetResizeGrabXR = false;
    this.targetResizeGrabYR = false;
    this.targetResizeGrabXL = false;
    this.targetResizeGrabYL = false;
    this.targetInner = undefined;
    this.targetRect = undefined;
    this.targetClass = undefined;
    this.corrigateCanvasPosition();
  }
  onMouseDown(e) {
    switch (this.editorService.clientModel.canvas.drawMode) {
      case 'class':
        this.drawClassMode(e);
        break;
      case 'cursor':
        this.cursorMode(e);
        break;
      case 'line':
        this.drawLineMode(e);
        break;
    }
  }
  @ViewChild('linecanvas') lineCanvasComponent: LineCanvasComponent;
  drawLineMode(e) {
    this.lineCanvasComponent.drawBegin(
      e,
      this.editorService.clientModel.lineCanvas.drawLineType
    );
  }
  cursorMode(e) {
    // EDIT BOX//
    if (e.target.className == 'edit-box') {
      let rect;
      let inner;
      rect = e.target.parentNode.getBoundingClientRect();
      inner = e.target.getBoundingClientRect();
      this.xdiff = e.clientX - inner.left;
      this.ydiff = e.clientY - inner.top;
      console.log(this.xdiff);
      console.log(this.ydiff);
      this.holding = true;
      this.clipDOM = e.target.closest('.edit-box-clip');
      // selection off
      this.editorService.clientModel.canvas.selectedClassIds = [];
      this.updateClassSelection();
    }
    // CLASS//
    if (
      e.target.className != 'edit-box' &&
      e.target.nodeName != 'INPUT' &&
      this.targetClass
    ) {
      console.log('selected a class');
      this.setTargets(e);
      this.setStoredTargets(e);
      // selection
      this.selectClickedClassOnly();
      this.updateClassSelection();
      // zindex problem
      const max = this.getHighestClassZIndex();
      if (this.targetClass) this.targetClass.z = max;
      // grabxr
      let n = 0;
      if (
        this.xdiff >=
        this.targetClass.scaledModel.width_scaled - this.resizePadding
      ) {
        n++;
        this.targetResizeGrabXR = true;
        this.targetCurrentId = this.targetClass.id;
        console.log('xl');
      }
      // grabxl

      if (this.xdiff < this.resizePadding) {
        n++;
        this.targetResizeGrabXL = true;
        this.targetCurrentId = this.targetClass.id;
      }
      // grabyr

      if (
        this.ydiff >=
        this.targetClass.scaledModel.height_scaled - this.resizePadding
      ) {
        n++;
        this.targetResizeGrabYR = true;
        this.targetCurrentId = this.targetClass.id;
      }
      // grabyl
      if (this.ydiff <= this.resizePadding) {
        n++;
        this.targetResizeGrabYL = true;
        this.targetCurrentId = this.targetClass.id;
      }
      if (n == 0) {
        this.holdingAny = true;
      }
    }
  }
  drawClassMode(e) {
    const rect = e.target.closest('.edit-box')?.getBoundingClientRect();
    if (!rect) return;
    const x = e.clientX - rect.left; // x position within the element.
    const y = e.clientY - rect.top;
    this.drawedClassX = x;
    this.drawedClassY = y;
    this.drawedClassId = `c${this.getNewClassId()}`;
    let newclass: SimpleClass;
    newclass = {
      id: this.drawedClassId,
      width: 1,
      height: 1,
      posx: this.drawedClassX,
      posy: this.drawedClassY,
      min_height: 75,
      scaledModel: {
        posx_scaled: x,
        posy_scaled: y,
        width_scaled: 1,
        height_scaled: 1,
        min_height_scaled: 75,
      },
      z: this.getHighestClassZIndex(),
      edit: false,
      name: '',
      class_type: 'classDG',
      groups: [
        {
          group_name: 'attributes',
          group_syntax: GROUP_SYNTAX.ATTRIBUTE,
          attributes: [],
        },
        {
          group_name: 'functions',
          group_syntax: GROUP_SYNTAX.FUNCTION,
          attributes: [],
        },
      ],
      titleModel: {
        edit: true,
        id: this.drawedClassId + '-t',
        name: 'Class',
        viewModel: null,
      },
    };
    this.editorService.model.classes.push(newclass);
    this.targetClass = newclass;
    this.drawedClassPositionSpecified = true;
  }
}
