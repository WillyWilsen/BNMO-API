package bnmo.bnmoapi.api.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bnmo.bnmoapi.classes.message.Message;
import bnmo.bnmoapi.classes.role.Role;
import bnmo.bnmoapi.classes.sql.users.read.UserDetailBySearch;
import bnmo.bnmoapi.classes.sql.users.read.UserRoleByToken;
import bnmo.bnmoapi.classes.token.Token;
import bnmo.bnmoapi.classes.users.UserInfo;

@RestController
@CrossOrigin(origins = {"http://localhost:3000", "https://bnmo.herokuapp.com"}, allowCredentials = "true")
@RequestMapping("api/admin")
public class CustomerSearch {
    
    @Autowired
    private JdbcTemplate db;

    @GetMapping("/customer-search/{name_to_search}")
    public ResponseEntity<?> searchCustomer(HttpServletRequest request, @PathVariable("name_to_search") String name_to_search) {
        Token token = new Token(request);
        Role role = new Role(token);
        try {
            role.setPermission(db.queryForObject(new UserRoleByToken(token.value).query(), String.class));
            if (role.isAdmin()) {
                List<UserInfo> searched_users = db.query(new UserDetailBySearch(name_to_search).query(), (rs, rowNum) -> new UserInfo(
                    rs.getString("nama"),
                    rs.getString("username"),
                    rs.getString("image"),
                    rs.getFloat("saldo")
                ));
                return ResponseEntity.ok(searched_users);
            }
        } catch (Exception e) {}
        return ResponseEntity.ok(new Message("Unauthorized"));
    }
}
