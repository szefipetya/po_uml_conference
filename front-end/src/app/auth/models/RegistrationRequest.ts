export class RegistrationRequest {
  constructor(un, fn, em, pw) {
    this.username = un;
    this.email = em;
    this.fullName = fn;
    this.password = pw;
  }
  username: string;
  email: string;
  fullName: string;
  password: string;
}
