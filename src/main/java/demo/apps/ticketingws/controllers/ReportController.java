package demo.apps.ticketingws.controllers;

/**
 * @author jsa000y
 */

import demo.apps.ticketingws.models.report.ReportBySectionResponseDTO;
import demo.apps.ticketingws.models.train.TrainSection;
import demo.apps.ticketingws.services.ReportServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {

    @Autowired
    ReportServices reportServices;

    /**
     * This endpoint can be used for the reporting purpose
     *
     * @param section parameter takes in the section as an input.
     * @return the user details and ticket details is a particular seat is allocated.
     * If no ticket is allocated it will return an empty list
     */
    @GetMapping("/by-section")
    public List<ReportBySectionResponseDTO> getBySection(@RequestParam(name = "section") @NotNull TrainSection section) {
        return reportServices.generateReportBySection(section);
    }
}
