export interface SocketWrapper {
  socket: any;
  onmessage(m: any);
  onopen(m: any);
  onclose(m: any);
  connect(source: string);
  disconnect();
}
