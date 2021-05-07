CREATE FUNCTION del_expired_black_tokens() RETURNS trigger AS $del_expired_black_tokens$
    BEGIN
      DELETE FROM black_jwt WHERE expiration > NOW();
  RETURN NULL;
    END;
$del_expired_black_tokens$ LANGUAGE plpgsql;

CREATE TRIGGER del_expired_black_tokens AFTER INSERT OR UPDATE ON black_jwt
    FOR EACH ROW EXECUTE PROCEDURE del_expired_black_tokens();