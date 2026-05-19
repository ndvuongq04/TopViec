import type { CvOnlineExtraData } from '@/types/cvOnline.types'

type RecordValue = Record<string, unknown>

function escapeHtml(value: unknown) {
    return String(value ?? '')
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
}

function renderCollection(template: string, section: string, items: unknown[]) {
    const pattern = new RegExp(`{{#${section}}}([\\s\\S]*?){{\\/${section}}}`, 'g')

    return template.replace(pattern, (_, block: string) => {
        if (!Array.isArray(items) || items.length === 0) {
            return ''
        }

        return items
            .map((item) => {
                const record = (item ?? {}) as RecordValue
                return block.replace(/{{(\w+)}}/g, (_match: string, key: string) => {
                    return escapeHtml(record[key])
                })
            })
            .join('')
    })
}

function flattenRootPlaceholders(data: CvOnlineExtraData) {
    return {
        ...data.personalInfo,
        careerObjective: data.careerObjective,
    }
}

export function renderCvTemplateHtml(templateHtml: string, data: CvOnlineExtraData) {
    let output = templateHtml

    output = renderCollection(output, 'experiences', data.experiences)
    output = renderCollection(output, 'educations', data.educations)
    output = renderCollection(output, 'skills', data.skills)
    output = renderCollection(output, 'certifications', data.certifications)
    output = renderCollection(output, 'languages', data.languages)
    output = renderCollection(output, 'projects', data.projects)
    output = renderCollection(output, 'hobbies', data.hobbies)
    output = renderCollection(output, 'awards', data.awards)
    output = renderCollection(output, 'customSections', data.customSections)

    const rootValues = flattenRootPlaceholders(data)
    output = output.replace(/{{(\w+)}}/g, (_match: string, key: keyof typeof rootValues) => {
        return escapeHtml(rootValues[key])
    })

    return output
}

export function buildCvPreviewDocument(
    templateHtml: string,
    templateCss: string,
    data: CvOnlineExtraData,
) {
    const renderedHtml = renderCvTemplateHtml(templateHtml, data)
    return `<!DOCTYPE html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0" />
    <style>${templateCss}</style>
  </head>
  <body>${renderedHtml}</body>
</html>`
}
