package org.jarvis.agent.core.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeywordCount {
    /** 关键词 */
    private String word;
    /** 出现频次 */
    private int count;
}
