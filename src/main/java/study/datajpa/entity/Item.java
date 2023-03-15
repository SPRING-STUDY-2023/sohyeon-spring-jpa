package study.datajpa.entity;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import lombok.Getter;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
public class Item implements Persistable<String> {

	@Id @GeneratedValue
	private Long id;

	@CreatedDate
	private LocalDateTime createdDate;

	@Override
	public boolean isNew() {
		return createdDate == null;
	}
}
