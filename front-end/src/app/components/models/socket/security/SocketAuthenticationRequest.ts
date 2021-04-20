export class SocketAuthenticationRequest {
  auth_jwt: string;
  diagram_id: number;
  constructor(auth, diag) {
    this.auth_jwt = auth;
    this.diagram_id = diag;
  }
}
