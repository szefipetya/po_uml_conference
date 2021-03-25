export class CallbackItem {
  constructor(action_id) {
    this.action_id = action_id;
    this.pending = true;
  }
  action_id: string;
  pending: boolean;
}
