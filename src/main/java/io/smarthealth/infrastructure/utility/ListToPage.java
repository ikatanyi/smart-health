package io.smarthealth.infrastructure.utility;


import io.smarthealth.clinical.record.data.WaitingRequestsData;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.toIntExact;


public class ListToPage {

    public static Page<WaitingRequestsData> map(List<WaitingRequestsData> content, Integer offset, Integer limit) {
        System.out.println("Offset "+offset);
        System.out.println("Limit "+limit);
        PageRequest pageRequest = null;
        if (offset == null || limit == null) {
            System.out.println("Offset is null or limit is null");
            pageRequest = PageRequest.of(0, 1000000);
        } else {
            System.out.println("On else");
            pageRequest= PageRequest.of(offset, limit);
        }

        int total = content.size();
        int start = toIntExact(pageRequest.getOffset());
        int end = Math.min((start + pageRequest.getPageSize()), total);

        List<WaitingRequestsData> output = new ArrayList<>();

        if (start <= end) {
            output = content.subList(start, end);
        }

        return new PageImpl<>(
                output,
                pageRequest,
                total
        );


    }

}
