package org.hisrc.bahnmap.timetable.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "Item not found.")
public class ItemNotFoundException extends IllegalArgumentException {
	private static final long serialVersionUID = 451088854951301316L;

}
