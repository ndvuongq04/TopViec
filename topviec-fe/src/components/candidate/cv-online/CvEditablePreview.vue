<template>
  <div class="cv-editable-preview">
    <component :is="'style'" v-if="templateCss" v-text="templateCss" />

    <div class="cv-editable-preview__toolbar" v-show="activeEditable">
      <button type="button" title="Dam" @mousedown.prevent="formatSelection('bold')">
        <span class="material-symbols-outlined">format_bold</span>
      </button>
      <button type="button" title="Nghieng" @mousedown.prevent="formatSelection('italic')">
        <span class="material-symbols-outlined">format_italic</span>
      </button>
      <button type="button" title="Gach chan" @mousedown.prevent="formatSelection('underline')">
        <span class="material-symbols-outlined">format_underlined</span>
      </button>
      <span class="toolbar-divider" />
      <label title="Doi mau">
        <span class="material-symbols-outlined">palette</span>
        <input type="color" value="#0f172a" @input="setSelectionColor">
      </label>
      <span class="toolbar-divider" />
      <button type="button" title="Can trai" @mousedown.prevent="formatSelection('justifyLeft')">
        <span class="material-symbols-outlined">format_align_left</span>
      </button>
      <button type="button" title="Can giua" @mousedown.prevent="formatSelection('justifyCenter')">
        <span class="material-symbols-outlined">format_align_center</span>
      </button>
      <button type="button" title="Can phai" @mousedown.prevent="formatSelection('justifyRight')">
        <span class="material-symbols-outlined">format_align_right</span>
      </button>
    </div>

    <div class="cv-editable-preview__viewport">
      <div
        ref="paperRef"
        class="cv-template-edit-shell"
        v-html="editableDocument"
        @blur.capture="handleEditableBlur"
        @click.capture="handleShellClick"
        @focusin.capture="handleFocusIn"
        @focusout.capture="handleFocusOut"
        @keydown.capture="handleEditableKeydown"
      />
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'
import { useCvOnlineEditorStore } from '@/stores/cvOnlineEditor.store'
import type { CvOnlineExtraData, CvTemplateDetail } from '@/types/cvOnline.types'

type ListSectionKey =
  | 'experiences'
  | 'educations'
  | 'skills'
  | 'certifications'
  | 'languages'
  | 'projects'
  | 'hobbies'
  | 'awards'
  | 'customSections'

type EditableKind = 'personalInfo' | 'careerObjective' | 'section'
type SectionRecord = Record<string, unknown> & { id?: string }

const SECTION_KEYS: ListSectionKey[] = [
  'experiences',
  'educations',
  'skills',
  'certifications',
  'languages',
  'projects',
  'hobbies',
  'awards',
  'customSections',
]

const PERSONAL_PLACEHOLDERS: Record<string, string> = {
  fullName: 'Ho va ten',
  headline: 'Chuc danh',
  email: 'Email',
  phone: 'So dien thoai',
  address: 'Dia chi',
  city: 'Thanh pho',
  country: 'Quoc gia',
  website: 'Website',
  linkedin: 'LinkedIn',
  github: 'GitHub',
}

const SECTION_PLACEHOLDERS: Record<ListSectionKey, Record<string, string>> = {
  experiences: {
    jobTitle: 'Chuc danh',
    company: 'Cong ty',
    location: 'Dia diem',
    startDate: 'Bat dau',
    endDate: 'Ket thuc',
    description: 'Mo ta cong viec',
  },
  educations: {
    school: 'Truong hoc',
    degree: 'Bang cap',
    fieldOfStudy: 'Chuyen nganh',
    startDate: 'Bat dau',
    endDate: 'Ket thuc',
    description: 'Mo ta hoc van',
  },
  skills: {
    name: 'Ten ky nang',
    level: 'Trinh do',
    description: 'Mo ta ky nang',
  },
  certifications: {
    name: 'Ten chung chi',
    issuer: 'Don vi cap',
    issuedAt: 'Ngay cap',
    expiresAt: 'Het han',
    credentialId: 'Credential ID',
    credentialUrl: 'Credential URL',
    description: 'Mo ta chung chi',
  },
  languages: {
    name: 'Ngon ngu',
    level: 'Trinh do',
    certificate: 'Chung chi',
  },
  projects: {
    name: 'Ten du an',
    role: 'Vai tro',
    organization: 'To chuc / khach hang',
    startDate: 'Bat dau',
    endDate: 'Ket thuc',
    projectUrl: 'Project URL',
    description: 'Mo ta du an',
  },
  hobbies: {
    name: 'So thich',
    description: 'Mo ta',
  },
  awards: {
    title: 'Ten giai thuong / thanh tich',
    issuer: 'Don vi trao',
    awardedAt: 'Thoi gian nhan',
    description: 'Mo ta',
  },
  customSections: {
    sectionTitle: 'Ten section',
    itemTitle: 'Tieu de item',
    itemSubtitle: 'Phu de',
    itemMeta: 'Meta / thoi gian',
    description: 'Noi dung',
  },
}

const SECTION_ADD_LABELS: Record<ListSectionKey, string> = {
  experiences: 'Them kinh nghiem',
  educations: 'Them hoc van',
  skills: 'Them ky nang',
  certifications: 'Them chung chi',
  languages: 'Them ngon ngu',
  projects: 'Them du an',
  hobbies: 'Them so thich',
  awards: 'Them giai thuong',
  customSections: 'Them section',
}

const props = defineProps<{
  template: CvTemplateDetail
  data: CvOnlineExtraData
}>()

const editorStore = useCvOnlineEditorStore()
const paperRef = ref<HTMLElement | null>(null)
const activeEditable = ref<HTMLElement | null>(null)

const templateCss = computed(() => {
  return (props.template.cssContent || '').replace(
    /(^|[,{]\s*)(html|body)(?=([\s.#:[>+~,{]|$))/gi,
    (_match, prefix: string) => `${prefix}.cv-template-edit-shell`,
  )
})

const editableDocument = computed(() => buildEditableTemplate(props.template.htmlContent, props.data))

function escapeHtml(value: unknown) {
  return String(value ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#39;')
}

function escapeAttribute(value: unknown) {
  return escapeHtml(value).replace(/`/g, '&#96;')
}

function hasHtml(value: string) {
  return /<\/?[a-z][\s\S]*>/i.test(value)
}

function unwrapEditorParagraphs(value: string) {
  return value
    .replace(/^<p>([\s\S]*)<\/p>$/i, '$1')
    .replace(/<\/p>\s*<p>/gi, '<br>')
    .replace(/^<p>/i, '')
    .replace(/<\/p>$/i, '')
}

function valueToHtml(value: unknown) {
  const raw = unwrapEditorParagraphs(String(value ?? ''))
  if (!raw) return ''
  if (hasHtml(raw)) return raw
  return escapeHtml(raw).replace(/\n/g, '<br>')
}

function editableToken(options: {
  kind: EditableKind
  field?: string
  section?: ListSectionKey
  index?: number
  value: unknown
  placeholder: string
  singleLine: boolean
}) {
  const value = valueToHtml(options.value)
  const sectionAttrs = options.section !== undefined
    ? ` data-section="${options.section}" data-index="${options.index ?? 0}"`
    : ''
  const fieldAttr = options.field ? ` data-field="${escapeAttribute(options.field)}"` : ''

  return `<span
    class="cv-editable-token"
    contenteditable="true"
    data-cv-editable="true"
    data-kind="${options.kind}"
    ${fieldAttr}
    ${sectionAttrs}
    data-single-line="${options.singleLine ? 'true' : 'false'}"
    data-placeholder="${escapeAttribute(options.placeholder)}"
  >${value}</span>`
}

function renderBlockControls(section: ListSectionKey, index: number, total: number) {
  const upDisabled = index === 0 ? ' disabled' : ''
  const downDisabled = index === total - 1 ? ' disabled' : ''

  return `<div class="cv-block-controls">
    <button type="button" data-action="moveUp" data-section="${section}" data-index="${index}" title="Di chuyen len"${upDisabled}>
      <span class="material-symbols-outlined">arrow_upward</span>
    </button>
    <button type="button" data-action="moveDown" data-section="${section}" data-index="${index}" title="Di chuyen xuong"${downDisabled}>
      <span class="material-symbols-outlined">arrow_downward</span>
    </button>
    <button type="button" data-action="remove" data-section="${section}" data-index="${index}" title="Xoa">
      <span class="material-symbols-outlined">close</span>
    </button>
  </div>`
}

function renderAddButton(section: ListSectionKey) {
  return `<div class="cv-section-add-row">
    <button type="button" class="cv-section-add-btn" data-action="add" data-section="${section}">
      <span class="material-symbols-outlined">add</span>
      ${escapeHtml(SECTION_ADD_LABELS[section])}
    </button>
  </div>`
}

function renderSectionBlock(section: ListSectionKey, block: string, item: SectionRecord, index: number, total: number) {
  const labels = SECTION_PLACEHOLDERS[section]
  const renderedBlock = block.replace(/{{(\w+)}}/g, (_match, key: string) => {
    const value = item[key]
    if (typeof value === 'boolean') return escapeHtml(value ? 'Hien tai' : '')

    return editableToken({
      kind: 'section',
      section,
      index,
      field: key,
      value,
      placeholder: labels[key] ?? key,
      singleLine: !['description', 'content'].includes(key),
    })
  })

  return `<div class="cv-editable-block" data-cv-block="true" data-section="${section}" data-index="${index}">
    ${renderBlockControls(section, index, total)}
    ${renderedBlock}
  </div>`
}

function sectionItems(data: CvOnlineExtraData, section: ListSectionKey) {
  return data[section] as unknown as SectionRecord[]
}

function renderCollection(template: string, section: ListSectionKey, data: CvOnlineExtraData) {
  const pattern = new RegExp(`{{#${section}}}([\\s\\S]*?){{\\/${section}}}`, 'g')
  return template.replace(pattern, (_match, block: string) => {
    const items = sectionItems(data, section)
    const renderedItems = items
      .map((item, index) => renderSectionBlock(section, block, item, index, items.length))
      .join('')

    return `${renderedItems}${renderAddButton(section)}`
  })
}

function buildEditableTemplate(templateHtml: string, data: CvOnlineExtraData) {
  let output = templateHtml || ''

  for (const section of SECTION_KEYS) {
    output = renderCollection(output, section, data)
  }

  output = output.replace(/{{(\w+)}}/g, (_match, key: string) => {
    if (key === 'careerObjective') {
      return editableToken({
        kind: 'careerObjective',
        value: data.careerObjective,
        placeholder: 'Muc tieu nghe nghiep',
        singleLine: false,
      })
    }

    if (key in PERSONAL_PLACEHOLDERS) {
      return editableToken({
        kind: 'personalInfo',
        field: key,
        value: data.personalInfo[key as keyof CvOnlineExtraData['personalInfo']],
        placeholder: PERSONAL_PLACEHOLDERS[key] ?? key,
        singleLine: true,
      })
    }

    return ''
  })

  return output
}

function closestEditable(target: EventTarget | null) {
  if (!(target instanceof HTMLElement)) return null
  return target.closest('[data-cv-editable="true"]') as HTMLElement | null
}

function commitEditable(editable: HTMLElement) {
  const kind = editable.dataset.kind as EditableKind | undefined
  const field = editable.dataset.field
  const section = editable.dataset.section as ListSectionKey | undefined
  const index = Number(editable.dataset.index)
  const singleLine = editable.dataset.singleLine === 'true'
  const value = singleLine ? editable.innerText.trim() : editable.innerHTML.trim()

  if (kind === 'personalInfo' && field && field in PERSONAL_PLACEHOLDERS) {
    editorStore.patchPersonalInfo(field as keyof CvOnlineExtraData['personalInfo'], value)
    return
  }

  if (kind === 'careerObjective') {
    editorStore.updateCareerObjective(value)
    return
  }

  if (kind === 'section' && section && field && Number.isFinite(index)) {
    updateSectionField(section, index, field, value)
  }
}

function updateSectionField(section: ListSectionKey, index: number, field: string, value: string) {
  const patch = { [field]: value }

  switch (section) {
    case 'experiences':
      editorStore.updateExperience(index, patch as any)
      break
    case 'educations':
      editorStore.updateEducation(index, patch as any)
      break
    case 'skills':
      editorStore.updateSkill(index, patch as any)
      break
    case 'certifications':
      editorStore.updateCertification(index, patch as any)
      break
    case 'languages':
      editorStore.updateLanguage(index, patch as any)
      break
    case 'projects':
      editorStore.updateProject(index, patch as any)
      break
    case 'hobbies':
      editorStore.updateHobby(index, patch as any)
      break
    case 'awards':
      editorStore.updateAward(index, patch as any)
      break
    case 'customSections':
      editorStore.updateCustomSection(index, patch as any)
      break
  }
}

function addSectionItem(section: ListSectionKey) {
  switch (section) {
    case 'experiences':
      editorStore.addExperience()
      break
    case 'educations':
      editorStore.addEducation()
      break
    case 'skills':
      editorStore.addSkill()
      break
    case 'certifications':
      editorStore.addCertification()
      break
    case 'languages':
      editorStore.addLanguage()
      break
    case 'projects':
      editorStore.addProject()
      break
    case 'hobbies':
      editorStore.addHobby()
      break
    case 'awards':
      editorStore.addAward()
      break
    case 'customSections':
      editorStore.addCustomSection()
      break
  }
}

function removeSectionItem(section: ListSectionKey, index: number) {
  switch (section) {
    case 'experiences':
      editorStore.removeExperience(index)
      break
    case 'educations':
      editorStore.removeEducation(index)
      break
    case 'skills':
      editorStore.removeSkill(index)
      break
    case 'certifications':
      editorStore.removeCertification(index)
      break
    case 'languages':
      editorStore.removeLanguage(index)
      break
    case 'projects':
      editorStore.removeProject(index)
      break
    case 'hobbies':
      editorStore.removeHobby(index)
      break
    case 'awards':
      editorStore.removeAward(index)
      break
    case 'customSections':
      editorStore.removeCustomSection(index)
      break
  }
}

function handleEditableBlur(event: FocusEvent) {
  const editable = closestEditable(event.target)
  if (!editable) return
  commitEditable(editable)
}

function handleEditableKeydown(event: KeyboardEvent) {
  const editable = closestEditable(event.target)
  if (!editable) return
  if (editable.dataset.singleLine !== 'true') return
  if (event.key !== 'Enter') return

  event.preventDefault()
  editable.blur()
}

function handleFocusIn(event: FocusEvent) {
  activeEditable.value = closestEditable(event.target)
}

function handleFocusOut() {
  window.setTimeout(() => {
    const activeElement = document.activeElement
    if (activeElement && paperRef.value?.contains(activeElement)) return
    activeEditable.value = null
  }, 120)
}

function handleShellClick(event: MouseEvent) {
  const target = event.target
  if (!(target instanceof HTMLElement)) return

  const button = target.closest('[data-action]') as HTMLButtonElement | null
  if (!button || button.disabled) return

  const action = button.dataset.action
  const section = button.dataset.section as ListSectionKey | undefined
  const index = Number(button.dataset.index)
  if (!section) return

  event.preventDefault()
  event.stopPropagation()

  if (action === 'add') {
    addSectionItem(section)
    return
  }

  if (!Number.isFinite(index)) return

  if (action === 'remove') {
    removeSectionItem(section, index)
    return
  }

  if (action === 'moveUp') {
    editorStore.moveItemInSection(section, index, index - 1)
    return
  }

  if (action === 'moveDown') {
    editorStore.moveItemInSection(section, index, index + 1)
  }
}

function formatSelection(command: string) {
  activeEditable.value?.focus()
  document.execCommand(command)
}

function setSelectionColor(event: Event) {
  const color = (event.target as HTMLInputElement).value
  if (!color) return
  activeEditable.value?.focus()
  document.execCommand('foreColor', false, color)
}
</script>

<style scoped>
.cv-editable-preview {
  position: relative;
}

.cv-editable-preview__viewport {
  overflow-x: auto;
  border: 1px solid #dbe2ea;
  border-radius: 18px;
  background: #e5e7eb;
  padding: 1.25rem;
}

.cv-template-edit-shell {
  width: min(100%, 920px);
  min-height: 1120px;
  margin: 0 auto;
  background: #fff;
  color: #0f172a;
  box-shadow: 0 24px 70px rgba(15, 23, 42, 0.18);
}

.cv-editable-preview__toolbar {
  position: sticky;
  top: 0.75rem;
  z-index: 20;
  display: inline-flex;
  align-items: center;
  gap: 0.25rem;
  border: 1px solid rgba(15, 23, 42, 0.1);
  border-radius: 12px;
  background: rgba(255, 255, 255, 0.96);
  padding: 0.35rem;
  box-shadow: 0 18px 42px rgba(15, 23, 42, 0.16);
  backdrop-filter: blur(12px);
  margin-bottom: 0.75rem;
}

.cv-editable-preview__toolbar button,
.cv-editable-preview__toolbar label {
  position: relative;
  display: inline-flex;
  width: 2rem;
  height: 2rem;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: #475569;
  cursor: pointer;
  transition: background-color 0.15s ease, color 0.15s ease;
}

.cv-editable-preview__toolbar button:hover,
.cv-editable-preview__toolbar label:hover {
  background: #e0f2fe;
  color: #0369a1;
}

.cv-editable-preview__toolbar input {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  cursor: pointer;
  opacity: 0;
}

.cv-editable-preview__toolbar .material-symbols-outlined {
  font-size: 1.2rem;
}

.toolbar-divider {
  width: 1px;
  height: 1.35rem;
  background: #e2e8f0;
  margin: 0 0.15rem;
}

@media (max-width: 640px) {
  .cv-editable-preview__viewport {
    padding: 0.75rem;
  }

  .cv-template-edit-shell {
    min-height: 920px;
  }
}
</style>

<style>
.cv-template-edit-shell [data-cv-editable='true'] {
  min-width: 1ch;
  border: 1px dashed transparent;
  border-radius: 4px;
  padding: 1px 4px;
  cursor: text;
  transition: border-color 0.16s ease, background-color 0.16s ease, box-shadow 0.16s ease;
}

.cv-template-edit-shell [data-cv-editable='true'][data-single-line='false'] {
  display: block;
  min-height: 1.2em;
}

.cv-template-edit-shell [data-cv-editable='true']:hover {
  border-color: #cbd5e1;
}

.cv-template-edit-shell [data-cv-editable='true']:focus {
  border-color: #3b82f6;
  background: rgba(59, 130, 246, 0.04);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.08);
  outline: none;
}

.cv-template-edit-shell [data-cv-editable='true']:empty::before {
  content: attr(data-placeholder);
  color: #94a3b8;
  pointer-events: none;
}

.cv-editable-block {
  position: relative;
  border: 1px solid transparent;
  border-radius: 8px;
  padding: 0.25rem 2.5rem 0.25rem 0.25rem;
  transition: border-color 0.16s ease, background-color 0.16s ease;
}

.cv-editable-block:hover {
  border-color: rgba(148, 163, 184, 0.35);
  background: rgba(248, 250, 252, 0.68);
}

.cv-block-controls {
  position: absolute;
  top: 0.25rem;
  right: 0.25rem;
  z-index: 5;
  display: none;
  gap: 0.2rem;
  border: 1px solid rgba(148, 163, 184, 0.35);
  border-radius: 999px;
  background: rgba(255, 255, 255, 0.96);
  padding: 0.2rem;
  box-shadow: 0 12px 30px rgba(15, 23, 42, 0.12);
  backdrop-filter: blur(10px);
}

.cv-editable-block:hover .cv-block-controls {
  display: inline-flex;
}

.cv-block-controls button {
  display: inline-flex;
  width: 1.65rem;
  height: 1.65rem;
  align-items: center;
  justify-content: center;
  border: none;
  border-radius: 999px;
  background: transparent;
  color: #475569;
  cursor: pointer;
}

.cv-block-controls button:hover:not(:disabled) {
  background: #f1f5f9;
  color: #0f172a;
}

.cv-block-controls button:disabled {
  cursor: default;
  opacity: 0.35;
}

.cv-block-controls .material-symbols-outlined,
.cv-section-add-btn .material-symbols-outlined {
  font-size: 1rem;
}

.cv-section-add-row {
  margin-top: 0.6rem;
}

.cv-section-add-btn {
  display: inline-flex;
  align-items: center;
  gap: 0.35rem;
  border: 1px dashed #94a3b8;
  border-radius: 999px;
  background: #fff;
  padding: 0.4rem 0.75rem;
  color: #334155;
  font: inherit;
  font-size: 0.86rem;
  font-weight: 700;
  cursor: pointer;
}

.cv-section-add-btn:hover {
  border-color: #0ea5e9;
  background: #f0f9ff;
  color: #0369a1;
}
</style>
