export class ActionTarget {
  constructor(id = '--', _type = '--', parent_id = '--') {
    this.target_id = id;
    this.parent_id = parent_id;
    this._type = _type;
  }
  target_id: string;
  parent_id: string;
  _type: string;
}
