package com.edifecs.discussionforum.jpa.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sandeep.kath on 5/14/2014.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = TopicEntity.FIND_ALL_TOPICS, query = "SELECT topic FROM TopicEntity topic where topic.category=:category")
})
public class TopicEntity extends AuditObject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String subject;

    private Date postedDate;

    @ManyToOne(cascade = CascadeType.ALL)
    private CategoryEntity category;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ReplyEntity> replies = new ArrayList<ReplyEntity>();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public CategoryEntity getCategory() {
        return category;
    }

    public void setCategory(CategoryEntity category) {
        this.category = category;
    }

    public List<ReplyEntity> getReplies() {
        return replies;
    }

    public void setReplies(List<ReplyEntity> replies) {
        this.replies = replies;
    }

    public static final String FIND_ALL_TOPICS = "FindAll.topics";
}
