package controllers;

import play.mvc.With;

@With(Secure.class)
@Check("super-admin")
public class Avatars extends CRUD {

}
