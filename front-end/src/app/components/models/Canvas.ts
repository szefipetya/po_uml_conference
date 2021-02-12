export class Canvas{
  edit_element_id:number;
      edit_classTitle_id: number;
      scale:number;
      posx: number;
      posy:number;
      width: number;
      height: number;
      gridSize: number;
      drawMode: string;
      drawRect: {
        x: 0,
        y: 0,
        width: 0,
        height: 0,
      };
      selectedClassIds: string[];
}
