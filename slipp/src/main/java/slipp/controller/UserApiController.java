package slipp.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nextstep.annotation.Inject;
import nextstep.mvc.JsonView;
import nextstep.mvc.ModelAndView;
import nextstep.stereotype.Controller;
import nextstep.web.annotation.RequestMapping;
import nextstep.web.annotation.RequestMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import slipp.dao.UserDao;
import slipp.domain.User;
import slipp.dto.UserCreatedDto;
import slipp.dto.UserUpdatedDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class UserApiController {
    private static final Logger log = LoggerFactory.getLogger(UserApiController.class);

    private final ObjectMapper objectMapper;
    private final UserDao userDao;

    @Inject
    public UserApiController(UserDao userDao) {
        log.debug("begin");

        this.userDao = userDao;
        this.objectMapper = new ObjectMapper();
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.POST)
    public ModelAndView create(HttpServletRequest request, HttpServletResponse response) throws Exception {
        UserCreatedDto createdDto = objectMapper.readValue(request.getInputStream(), UserCreatedDto.class);
        log.debug("Created User : {}", createdDto);

        userDao.insert(new User(
                createdDto.getUserId(),
                createdDto.getPassword(),
                createdDto.getName(),
                createdDto.getEmail()));

        response.setHeader("Location", "/api/users?userId=" + createdDto.getUserId());
        response.setStatus(HttpStatus.CREATED.value());

        return new ModelAndView(new JsonView());
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.GET)
    public ModelAndView show(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        log.debug("userId : {}", userId);

        ModelAndView mav = new ModelAndView(new JsonView());
        mav.addObject("user", userDao.findByUserId(userId));
        return mav;
    }

    @RequestMapping(value = "/api/users", method = RequestMethod.PUT)
    public ModelAndView update(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String userId = request.getParameter("userId");
        log.debug("userId : {}", userId);
        UserUpdatedDto updateDto = objectMapper.readValue(request.getInputStream(), UserUpdatedDto.class);
        log.debug("Updated User : {}", updateDto);

        User user = userDao.findByUserId(userId);
        user.update(updateDto);
        userDao.update(user);

        return new ModelAndView(new JsonView());
    }
}
