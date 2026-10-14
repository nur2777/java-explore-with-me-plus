package ru.practicum.events.pagination;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class OffsetBasedPageRequest implements Pageable {

    private final long offset;
    private final int pageSize;
    private final Sort sort;

    public OffsetBasedPageRequest(long offset, int pageSize, Sort sort) {
        if (offset < 0 || pageSize <= 0) {
            throw new IllegalArgumentException("Некорректные параметры пагинации");
        }

        this.offset = offset;
        this.pageSize = pageSize;
        this.sort = sort;
    }

    @Override
    public int getPageNumber() {
        return (int) (offset / pageSize);
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public Pageable next() {
        return new OffsetBasedPageRequest(offset + pageSize, pageSize, sort);
    }

    @Override
    public Pageable previousOrFirst() {
        return hasPrevious()
                ? new OffsetBasedPageRequest(
                Math.max(0, offset - pageSize), pageSize, sort)
                : first();
    }

    @Override
    public Pageable first() {
        return new OffsetBasedPageRequest(0, pageSize, sort);
    }

    @Override
    public Pageable withPage(int pageNumber) {
        return new OffsetBasedPageRequest(
                (long) pageNumber * pageSize, pageSize, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset > 0;
    }
}