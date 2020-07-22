package org.xcolab.view.pages.intercommunity.fusion;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.xcolab.view.util.pagination.SortFilterPage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IntercommunityController {
    @RequestMapping("/intercommunity")
    public String showPage(HttpServletRequest request, HttpServletResponse response, Model model,
            SortFilterPage sortFilterPage,
            @RequestParam(value = "page", required = false) Long pageParam) {
        model.addAttribute("_activePageLink", "intercommunity");
        return "intercommunity/fusion";
    }
}
