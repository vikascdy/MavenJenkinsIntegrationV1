package com.edifecs.discussionforum.jpa.entity;



import javax.persistence.*;
import java.util.Date;


/**
 * Created by sandeep.kath on 5/14/2014.
 */
@Entity
@NamedQueries({
        @NamedQuery(name = ReplyEntity.FIND_ALL_REPLIES, query = "SELECT reply FROM ReplyEntity reply where reply.topic=:topic")
})
public class ReplyEntity extends AuditObject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String message;
    Date replyDate;

    public TopicEntity getTopic() {
        return topic;
    }

    public void setTopic(TopicEntity topic) {
        this.topic = topic;
    }

    @OneToOne(cascade = CascadeType.ALL)
    TopicEntity topic;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;

    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getReplyDate() {
        return replyDate;
    }

    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }

public static final String FIND_ALL_REPLIES = "find.all.replies";


}
