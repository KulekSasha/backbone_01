package com.nix.tag;

import com.nix.model.User;
import com.nix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class UserListTag implements Tag {
    private PageContext pageContext;
    private Tag parent;

    @Autowired
    @Qualifier("userService")
    private UserService userService;

    @Override
    public void setPageContext(PageContext pc) {
        this.pageContext = pc;
    }

    @Override
    public void setParent(Tag t) {
        this.parent = t;
    }

    @Override
    public Tag getParent() {
        return parent;
    }

    @Override
    public int doStartTag() {

        if (userService == null) {
            init();
        }

        List<User> users = userService.findAll();
        String contextPath = ((HttpServletRequest) pageContext.getRequest()).getContextPath();

        JspWriter out = pageContext.getOut();
        StringBuilder sb = new StringBuilder();

        sb.append("<table class=\"table\">");

        sb.append("<thead>");
        sb.append("<tr bgcolor=\"#84878C\">");
        sb.append("<th>Login</th>");
        sb.append("<th>First Name</th>");
        sb.append("<th>Last Name</th>");
        sb.append("<th>Age</th>");
        sb.append("<th>Role</th>");
        sb.append("<th>Action</th>");
        sb.append("</tr>");
        sb.append("</thead>");

        sb.append("<tbody>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>").append(user.getLogin()).append("</td>");
            sb.append("<td>").append(user.getFirstName()).append("</td>");
            sb.append("<td>").append(user.getLastName()).append("</td>");
            sb.append("<td>").append(getAge(user.getBirthday())).append("</td>");
            sb.append("<td>").append(user.getRole().getName()).append("</td>");

            String linkEdit = "<a class=\"btn-sm btn-success\" role=\"button\"" +
                    " href=\"" + contextPath + "/admin/users/" +
                    user.getLogin() + "/edit" + "\">Edit</a> ";
            String linkDelete = "<button type=\"button\" class=\"btn-xs btn-danger\" value=\"" +
                    user.getLogin() + "\">Delete</button> ";

            sb.append("<td>").append(linkEdit).append(linkDelete).append("</td>");

            sb.append("</tr>");
        }
        sb.append("</tbody>");

        sb.append("</table>");

        try {
            out.print(sb.toString());
        } catch (IOException e) {
            System.err.println(e);
        }

        return SKIP_BODY;
    }


    @Override
    public int doEndTag() throws JspException {
        return 0;
    }

    @Override
    public void release() {

    }

    private int getAge(Date dateOfBirth) {
        Calendar dob = Calendar.getInstance();
        dob.setTime(dateOfBirth);
        Calendar today = Calendar.getInstance();
        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    private void init() {
        pageContext.getServletContext();
        WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(
                pageContext.getServletContext());
        wac.getAutowireCapableBeanFactory()
                .autowireBean(this);
    }
}
