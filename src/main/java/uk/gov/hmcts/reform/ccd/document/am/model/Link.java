package uk.gov.hmcts.reform.ccd.document.am.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * Link.
 */
@Validated
public class Link {
    @JsonProperty("deprecation")
    private String deprecation = null;

    @JsonProperty("href")
    private String href = null;

    @JsonProperty("hreflang")
    private String hreflang = null;

    @JsonProperty("media")
    private String media = null;

    @JsonProperty("rel")
    private String rel = null;

    @JsonProperty("templated")
    private transient boolean templated;

    @JsonProperty("title")
    private String title = null;

    @JsonProperty("type")
    private String type = null;


    /**
     * Get deprecation.
     *
     * @return deprecation
     **/
    @ApiModelProperty(value = "")

    public String getDeprecation() {
        return deprecation;
    }

    public void setDeprecation(String deprecation) {
        this.deprecation = deprecation;
    }

    /**
     * Get href.
     *
     * @return href
     **/
    @ApiModelProperty(value = "")

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    /**
     * Get hreflang.
     *
     * @return hreflang
     **/
    @ApiModelProperty(value = "")

    public String getHreflang() {
        return hreflang;
    }

    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    /**
     * Get media.
     *
     * @return media
     **/
    @ApiModelProperty(value = "")

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    /**
     * Get rel.
     *
     * @return rel
     **/
    @ApiModelProperty(value = "")

    public String getRel() {
        return rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }


    /**
     * Get templated.
     *
     * @return templated
     **/
    @ApiModelProperty(value = "")

    public Boolean isTemplated() {
        return templated;
    }

    public void setTemplated(Boolean templated) {
        this.templated = templated;
    }

    /**
     * Get title.
     *
     * @return title
     **/
    @ApiModelProperty(value = "")

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get type.
     *
     * @return type
     **/
    @ApiModelProperty(value = "")

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Link link = (Link) o;
        return Objects.equals(this.deprecation, link.deprecation)
               && Objects.equals(this.href, link.href)
               && Objects.equals(this.hreflang, link.hreflang)
               && Objects.equals(this.media, link.media)
               && Objects.equals(this.rel, link.rel)
               && Objects.equals(this.templated, link.templated)
               && Objects.equals(this.title, link.title)
               && Objects.equals(this.type, link.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(deprecation, href, hreflang, media, rel, templated, title, type);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("class Link {\n");

        sb.append("    deprecation: ").append(toIndentedString(deprecation)).append("\n");
        sb.append("    href: ").append(toIndentedString(href)).append("\n");
        sb.append("    hreflang: ").append(toIndentedString(hreflang)).append("\n");
        sb.append("    media: ").append(toIndentedString(media)).append("\n");
        sb.append("    rel: ").append(toIndentedString(rel)).append("\n");
        sb.append("    templated: ").append(toIndentedString(templated)).append("\n");
        sb.append("    title: ").append(toIndentedString(title)).append("\n");
        sb.append("    type: ").append(toIndentedString(type)).append("\n");
        sb.append("}");
        return sb.toString();
    }

    private String toIndentedString(Object o) {
        if (o == null) {
            return "null";
        }
        return o.toString().replace("\n", "\n    ");
    }
}
