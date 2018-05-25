package com.example.demo.message;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/topics/")
public class TopicController
{
    @RequestMapping(value = "index.do", method = RequestMethod.GET)
    public ModelAndView index(HttpSession session)
    {
        String username = getUserName(session);
        List<Object> openTopics = TopicManager.getInstance().all().filter(TopicManager.ofAuthorAndInLastTwoHour(username))
                .sorted(Comparator.comparing(Topic::getCreatedAt).reversed()).map(Topic::toMap).collect(Collectors.toList());
        ModelMap model = new ModelMap();
        model.put("topics", openTopics);
        return new ModelAndView(new MappingJackson2JsonView(), model);
    }

    @RequestMapping(value = "delete.do", method =  RequestMethod.POST)
    public ModelAndView delete(String id, HttpSession session)
    {
        String username = getUserName(session);
        Topic topic = TopicManager.getInstance().find(id);
        if(topic != null && topic.getAuthor() != null && topic.getAuthor().equals(username))
        {
            TopicManager.getInstance().delete(topic);
        }
        return new ModelAndView(new MappingJackson2JsonView());
    }

    @RequestMapping(value = "clean.do", method =  RequestMethod.POST)
    public ModelAndView clean(HttpSession session)
    {
        String username = getUserName(session);
        TopicManager.getInstance().clean(username);
        return new ModelAndView(new MappingJackson2JsonView());
    }

    public String getUserName(HttpSession session)
    {
        return "";
    }

}
