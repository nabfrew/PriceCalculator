package com.demo.model;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.LocalDate;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


/**
 * Adapted from <a href="https://github.com/peter-evans/jOOQ-pg-int8multirange">kotlin</a>
 * This feature is pretty fresh, there will hopefully be better ways of doing this soon enough.
 */
@Converter
public class DateRangeListConverter  implements AttributeConverter<List<DateRange>, String> {
    private static final Pattern REGEX = Pattern.compile("\\[(.*?),(.*?)\\)");

    @Override
    public String convertToDatabaseColumn(List<DateRange> dateRanges) {
        if (dateRanges == null) {
            return null;
        }

        return dateRanges.stream()
                .map(range -> "[" + range.start() + "," + (range.end() + 1) + ")")
                .collect(Collectors.joining(",", "{", "}"));
    }

    @Override
    public List<DateRange> convertToEntityAttribute(String dbData) {
        if (dbData == null || dbData.isEmpty()) {
            return List.of(new DateRange(LocalDate.MIN, LocalDate.MAX));
        }

        Matcher matcher = REGEX.matcher(dbData);

        return matcher.results()
                .map(DateRangeListConverter::getDateRange)
                .toList();
    }

    private static DateRange getDateRange(MatchResult matchResult) {
        long start = Long.parseLong(matchResult.group(1));
        long end = Long.parseLong(matchResult.group(2));
        return new DateRange(start, end - 1);
    }
}
